package com.codepeek.service.fileParser.interfaces;

import com.codepeek.service.models.DependencyGraph;

import java.io.File;

public interface FileParserInterface {
    void parseFile(File file, DependencyGraph graph);
}
