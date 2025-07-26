package com.codepeek.service.fileParser.service;

import com.codepeek.service.fileParser.interfaces.FileParserInterface;
import com.codepeek.service.models.DependencyGraph;
import com.codepeek.service.models.GraphEdge;
import com.codepeek.service.models.GraphNode;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Qualifier("javaFileParser")

public class JavaFileParserService implements FileParserInterface {

    @Override
    public void parseFile(File file, DependencyGraph graph) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);
            new MethodVisitor(graph).visit(cu, null);
        } catch (IOException e) {
            System.err.println("Error parsing file: " + file.getName());
            e.printStackTrace();
        }
    }

    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        private final DependencyGraph graph;
        private String currentClassName = null;
        private final Map<String, String> injectedServices = new HashMap<>();

        public MethodVisitor(DependencyGraph graph) {
            this.graph = graph;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (!n.isInterface()) {
                this.currentClassName = n.getNameAsString();

                // Handle field-based injection
                for (FieldDeclaration field : n.getFields()) {
                    if (field.isAnnotationPresent("Autowired")) {
                        String serviceClassName = field.getElementType().toString();
                        String serviceVariableName = field.getVariable(0).getNameAsString();
                        injectedServices.put(serviceVariableName, serviceClassName);
                    }
                }
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(ConstructorDeclaration constructor, Void arg) {
            if (currentClassName == null) return;

            // Handle constructor-based injection
            for (Parameter parameter : constructor.getParameters()) {
                String serviceClassName = parameter.getType().toString();
                String serviceVariableName = parameter.getNameAsString();
                injectedServices.put(serviceVariableName, serviceClassName);
            }

            super.visit(constructor, arg);
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (currentClassName == null) return;

            String methodName = n.getNameAsString();
            String fullMethodName = currentClassName + "." + methodName;

            graph.getNodes().add(new GraphNode(fullMethodName, methodName, currentClassName));

            n.findAll(MethodCallExpr.class).forEach(mce -> {
                mce.getScope().ifPresent(scope -> {
                    String calledOnObject = scope.toString();
                    if (injectedServices.containsKey(calledOnObject)) {
                        String targetServiceClass = injectedServices.get(calledOnObject);
                        String targetMethodName = mce.getNameAsString();
                        String fullTargetName = targetServiceClass + "." + targetMethodName;

                        if (graph.getNodes().stream().noneMatch(node -> node.getId().equals(fullTargetName))) {
                            graph.getNodes().add(new GraphNode(fullTargetName, targetMethodName, targetServiceClass));
                        }

                        graph.getEdges().add(new GraphEdge(fullMethodName, fullTargetName));
                    }
                });
            });

            super.visit(n, arg);
        }
    }
}
