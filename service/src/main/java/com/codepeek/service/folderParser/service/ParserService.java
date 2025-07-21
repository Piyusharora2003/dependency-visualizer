package com.codepeek.service.folderParser.service;

import com.codepeek.service.downloadFile.interfaces.DownloadFileServiceInterface;
import com.codepeek.service.fileManager.interfaces.FileManagerInterface;
import com.codepeek.service.fileParser.interfaces.FileParserInterface;
import com.codepeek.service.models.DependencyGraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class ParserService {

    private final DownloadFileServiceInterface downloadFileService;
    private final FileManagerInterface fileManager;
    private final FileParserInterface fileParserService;

    ParserService(@Qualifier("gitFileDownload") DownloadFileServiceInterface downloadFileService,
                  @Qualifier("gitFileManager") FileManagerInterface fileManager,
                  @Qualifier("javaFileParser") FileParserInterface fileParserService) {
        this.downloadFileService = downloadFileService;
        this.fileManager = fileManager;
        this.fileParserService = fileParserService;
    }


    public DependencyGraph getDependencyGraph(String fileUrl) {
        List<String> filePaths = fileManager.getFilesAddress(fileUrl);
        List<File> fileList = new ArrayList<>();
        List<String> errorFilePaths = new ArrayList<>();

        filePaths.parallelStream().forEach(filePath -> {
            try {
                File file = downloadFileService.getFileFromUrl(filePath);
                if (file.isFile()) {
                    fileList.add(file);
                }
            } catch (Exception e) {
                errorFilePaths.add(filePath);
                log.info("Error retrieving file path: {}", filePath);
            }
        });
        log.info("file parsing status:: success: {}, failure:{}", filePaths.size() - errorFilePaths.size(), errorFilePaths.size());
        // create a dependency graph
        DependencyGraph dependencyGraph = new DependencyGraph();

        fileList.parallelStream()
                .forEach(file -> fileParserService.parseFile(file, dependencyGraph));

        return dependencyGraph;
    }

}

//@Service
//class JavaParserService {
//
//    // --- Data Transfer Objects (DTOs) to represent the graph ---
//    public DependencyGraph parseFile(File javaFile) throws IOException {
//        CompilationUnit cu = StaticJavaParser.parse(javaFile);
//        DependencyGraph graph = new DependencyGraph();
//
//        // Use a visitor to traverse the Abstract Syntax Tree (AST)
//        new MethodVisitor(graph).visit(cu, null);
//
//        return graph;
//    }
//
//    /**
//     * A Visitor that explores the Java file's AST to build the graph.
//     */
//    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
//
//        private final DependencyGraph graph;
//        private String currentClassName = null;
//        // Maps the variable name of a service to its class name (e.g., "orderSvc" -> "OrderService")
//        private final Map<String, String> injectedServices = new HashMap<>();
//
//        public MethodVisitor(DependencyGraph graph) {
//            this.graph = graph;
//        }
//
//        @Override
//        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
//            // Find the main class name
//            if (!n.isInterface()) {
//                this.currentClassName = n.getNameAsString();
//
//                // First, find all injected dependencies for this class
//                for (FieldDeclaration field : n.getFields()) {
//                    if (field.isAnnotationPresent("Autowired")) {
//                        String serviceClassName = field.getElementType().toString();
//                        String serviceVariableName = field.getVariable(0).getNameAsString();
//                        injectedServices.put(serviceVariableName, serviceClassName);
//                    }
//                }
//            }
//            super.visit(n, arg);
//        }
//
//        @Override
//        public void visit(MethodDeclaration n, Void arg) {
//            if (currentClassName == null) {
//                return; // Skip methods outside a class
//            }
//
//            String methodName = n.getNameAsString();
//            String fullMethodName = currentClassName + "." + methodName;
//
//            // Add the current method as a node in the graph
//            graph.nodes.add(new GraphNode(fullMethodName, methodName, currentClassName));
//
//            // Now, find all method calls inside this method
//            n.findAll(MethodCallExpr.class).forEach(mce -> {
//                // Check if the call is on one of our injected services
//                mce.getScope().ifPresent(scope -> {
//                    String calledOnObject = scope.toString();
//                    if (injectedServices.containsKey(calledOnObject)) {
//                        String targetServiceClass = injectedServices.get(calledOnObject);
//                        String targetMethodName = mce.getNameAsString();
//                        String fullTargetName = targetServiceClass + "." + targetMethodName;
//
//                        // Add the target method as a node if it doesn't exist yet
//                        if (graph.nodes.stream().noneMatch(node -> node.getId().equals(fullTargetName))) {
//                            graph.nodes.add(new GraphNode(fullTargetName, targetMethodName, targetServiceClass));
//                        }
//
//                        // Add an edge from the current method to the one it calls
//                        graph.edges.add(new GraphEdge(fullMethodName, fullTargetName));
//                    }
//                });
//            });
//
//            super.visit(n, arg);
//        }
//    }
//}