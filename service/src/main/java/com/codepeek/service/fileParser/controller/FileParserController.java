package com.codepeek.service.fileParser.controller;

import com.codepeek.service.fileParser.model.FileInputData;
import com.codepeek.service.fileParser.service.FileParserService;
import com.github.javaparser.quality.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/file")
public class FileParserController {

    private final FileParserService fileParserService;

    FileParserController(FileParserService fileParserService) {
        this.fileParserService = fileParserService;
    }

    @PostMapping("/")
    Map<String, List<String>> getFileParseResult(@RequestBody @NotNull FileInputData fileInputData) {
        log.info("File parsing request received for url: {}", fileInputData.getFileUrl());
        return fileParserService.getFile(fileInputData.getFileUrl());
    }
}
