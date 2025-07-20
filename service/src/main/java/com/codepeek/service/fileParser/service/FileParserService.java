package com.codepeek.service.fileParser.service;

import com.codepeek.service.fileParser.interfaces.FileExtractorServiceInterface;
import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class FileParserService {

    private final FileExtractorServiceInterface fileExtractorService;
    private final JavaParser javaParser;

    FileParserService(@Qualifier("localFileParser") FileExtractorServiceInterface fileExtractorService) {
        this.fileExtractorService = fileExtractorService;
        this.javaParser = new JavaParser();
    }

    private String getFieldNameFromDeclaration(FieldDeclaration field) {
        return field.getElementType().toString();
    }

    public Map<String, List<String>> getFile(String fileUrl) {
        try {
            File file = fileExtractorService.getFileFromUrl(fileUrl);
            JavaParserService javaParserService = new JavaParserService();
            JavaParserService.DependencyGraph dependencyGraph = javaParserService.parseFile(file);
            System.out.println(dependencyGraph.getNodes());
            return null;
//            return this.getFileDependenciesMap(file);
        } catch (Exception e) {
            log.info("Error getting parsed file , {}", e.getMessage());
        }
        return null;
    }

//    public Map<String, List<String>> getFileDependenciesMap(File file) throws FileNotFoundException {
//        if (ObjectUtils.isEmpty(file)) {
//            return null;
//        }
//        Map<String, List<String>> methodDependencies = new HashMap<>();
//        Optional<CompilationUnit> cu = javaParser.parse(file).getResult();
//        if (cu.isEmpty()) {
//            return null;
//        }
//        List<FieldDeclaration> fields = cu.get().findAll(FieldDeclaration.class);
////        Map<String, List<String>> servicesUsed = fields.parallelStream().map(this::getFieldNameFromDeclaration).collect(Collectors.toSet());
////        log.info(servicesUsed.toString());
////        cu.get().findAll(MethodDeclaration.class).forEach(method -> {
////            String methodName = method.getNameAsString();
////            List<String> calls = method.findAll(MethodCallExpr.class).stream()
////                    .map(NodeWithSimpleName::getNameAsString)
////                    .toList();
////
////            log.info("Method: {} calls {}", methodName, calls);
////            methodDependencies.put(methodName, calls);
////        });
////
////        return methodDependencies;
////        return servicesUsed;
////    }
//    }
}

@Service
class JavaParserService {

    // --- Data Transfer Objects (DTOs) to represent the graph ---

    /**
     * Represents the entire dependency graph.
     */
    public static class DependencyGraph {
        private final List<GraphNode> nodes = new ArrayList<>();
        private final List<GraphEdge> edges = new ArrayList<>();

        public List<GraphNode> getNodes() {
            return nodes;
        }

        public List<GraphEdge> getEdges() {
            return edges;
        }
    }

    /**
     * Represents a single node in the graph (e.g., "Service1.function1").
     */
    public static class GraphNode {
        private final String id; // e.g., "OrderService.createOrder"
        private final String label; // e.g., "createOrder"
        private final String group; // e.g., "OrderService"

        public GraphNode(String id, String label, String group) {
            this.id = id;
            this.label = label;
            this.group = group;
        }

        // Getters...
        public String getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public String getGroup() {
            return group;
        }
    }

    /**
     * Represents a directed edge between two nodes.
     */
    public static class GraphEdge {
        private final String from; // ID of the source node
        private final String to;   // ID of the target node

        public GraphEdge(String from, String to) {
            this.from = from;
            this.to = to;
        }

        // Getters...
        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }


    /**
     * Main service function. Takes a Java file and returns its dependency graph.
     *
     * @param javaFile The .java file to be parsed.
     * @return A DependencyGraph object representing the call graph.
     * @throws IOException If the file cannot be read.
     */
    public DependencyGraph parseFile(File javaFile) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(javaFile);
        DependencyGraph graph = new DependencyGraph();

        // Use a visitor to traverse the Abstract Syntax Tree (AST)
        new MethodVisitor(graph).visit(cu, null);

        return graph;
    }

    /**
     * A Visitor that explores the Java file's AST to build the graph.
     */
    private static class MethodVisitor extends VoidVisitorAdapter<Void> {

        private final DependencyGraph graph;
        private String currentClassName = null;
        // Maps the variable name of a service to its class name (e.g., "orderSvc" -> "OrderService")
        private final Map<String, String> injectedServices = new HashMap<>();

        public MethodVisitor(DependencyGraph graph) {
            this.graph = graph;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            // Find the main class name
            if (!n.isInterface()) {
                this.currentClassName = n.getNameAsString();

                // First, find all injected dependencies for this class
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
        public void visit(MethodDeclaration n, Void arg) {
            if (currentClassName == null) {
                return; // Skip methods outside a class
            }

            String methodName = n.getNameAsString();
            String fullMethodName = currentClassName + "." + methodName;

            // Add the current method as a node in the graph
            graph.nodes.add(new GraphNode(fullMethodName, methodName, currentClassName));

            // Now, find all method calls inside this method
            n.findAll(MethodCallExpr.class).forEach(mce -> {
                // Check if the call is on one of our injected services
                mce.getScope().ifPresent(scope -> {
                    String calledOnObject = scope.toString();
                    if (injectedServices.containsKey(calledOnObject)) {
                        String targetServiceClass = injectedServices.get(calledOnObject);
                        String targetMethodName = mce.getNameAsString();
                        String fullTargetName = targetServiceClass + "." + targetMethodName;

                        // Add the target method as a node if it doesn't exist yet
                        if (graph.nodes.stream().noneMatch(node -> node.getId().equals(fullTargetName))) {
                            graph.nodes.add(new GraphNode(fullTargetName, targetMethodName, targetServiceClass));
                        }

                        // Add an edge from the current method to the one it calls
                        graph.edges.add(new GraphEdge(fullMethodName, fullTargetName));
                    }
                });
            });

            super.visit(n, arg);
        }
    }
}