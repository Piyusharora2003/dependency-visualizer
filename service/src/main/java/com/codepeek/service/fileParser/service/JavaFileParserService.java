package com.codepeek.service.fileParser.service;

import com.codepeek.service.fileParser.interfaces.FileParserInterface;
import com.codepeek.service.models.DependencyGraph;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Qualifier("javaFileParser")
public class JavaFileParserService implements FileParserInterface {
    public void parseFile(File file, DependencyGraph graph) {
    }
}
