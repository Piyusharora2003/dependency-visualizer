package com.codepeek.service.folderParser.controller;

import com.codepeek.service.folderParser.model.FileInputData;
import com.codepeek.service.folderParser.service.ParserService;
import com.codepeek.service.models.DependencyGraph;
import com.github.javaparser.quality.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/project")
public class ParserController {

    private final ParserService fileParserService;

    ParserController(ParserService fileParserService) {
        this.fileParserService = fileParserService;
    }

    @PostMapping("/uploadLink")
    DependencyGraph getParseResult(@RequestBody @NotNull FileInputData fileInputData) {
        return fileParserService.getDependencyGraph(fileInputData.getFileUrl());
    }
}
