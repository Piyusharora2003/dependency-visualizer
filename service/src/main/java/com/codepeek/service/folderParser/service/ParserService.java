package com.codepeek.service.folderParser.service;

import com.codepeek.service.downloadFile.interfaces.DownloadFileServiceInterface;
import com.codepeek.service.fileManager.interfaces.FileManagerInterface;
import com.codepeek.service.fileParser.interfaces.FileParserInterface;
import com.codepeek.service.folderParser.model.FileInputData;
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

    ParserService(@Qualifier("localFileParser") DownloadFileServiceInterface downloadFileService,
                  @Qualifier("localFileManager") FileManagerInterface localFileManager,
                  @Qualifier("javaFileParser") FileParserInterface fileParserService) {
        this.downloadFileService = downloadFileService;
        this.fileManager = localFileManager;
        this.fileParserService = fileParserService;
    }


    public DependencyGraph getDependencyGraph(FileInputData fileInputData) {
        List<String> filePaths = fileManager.getFilesAddress(fileInputData);
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

//"ParserController.getParseResult" "ParserService.getDependencyGraph"
//"ParserService.getDependencyGraph" "DownloadFileServiceInterface.getFileFromUrl"
//"ParserService.getDependencyGraph" "FileParserInterface.parseFile"
//"MethodVisitor.visit" "DependencyGraph.getNodes"
//"MethodVisitor.visit" "DependencyGraph.getNodes"
//"MethodVisitor.visit" "DependencyGraph.getNodes"
//"MethodVisitor.visit" "DependencyGraph.getEdges"