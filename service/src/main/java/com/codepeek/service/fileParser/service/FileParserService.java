package com.codepeek.service.fileParser.service;

import com.codepeek.service.fileParser.interfaces.FileExtractorServiceInterface;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class FileParserService {

    private final FileExtractorServiceInterface fileExtractorService;
    private final JavaParser javaParser;

    FileParserService(FileExtractorServiceInterface fileExtractorService) {
        this.fileExtractorService = fileExtractorService;
        this.javaParser = new JavaParser();
    }

    public Map<String, List<String>> getFile(String fileUrl) {
        try {
            File file = fileExtractorService.getFileFromUrl(fileUrl);
            return this.getFileDependenciesMap(file);
        } catch (Exception e) {
            log.info("Error getting parsed file , {}", e.getMessage());
        }
        return null;
    }

    public Map<String, List<String>> getFileDependenciesMap(File file) throws FileNotFoundException {
        if (ObjectUtils.isEmpty(file)) {
            return null;
        }
        Map<String, List<String>> methodDependencies = new HashMap<>();
        Optional<CompilationUnit> cu = javaParser.parse(file).getResult();
        if (cu.isEmpty()) {
            return null;
        }
        cu.get().findAll(MethodDeclaration.class).forEach(method -> {
            String methodName = method.getNameAsString();
            List<String> calls = method.findAll(MethodCallExpr.class).stream()
                    .map(NodeWithSimpleName::getNameAsString)
                    .toList();

            log.info("Method: {} calls {}", methodName, calls);
            methodDependencies.put(methodName, calls);
        });

        return methodDependencies;
    }
}
