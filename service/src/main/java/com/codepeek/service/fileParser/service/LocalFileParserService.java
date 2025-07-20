package com.codepeek.service.fileParser.service;

import com.codepeek.service.common.CustomException;
import com.codepeek.service.fileParser.interfaces.FileExtractorServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;

@Service
@Slf4j
@Qualifier("localFileParser")
public class LocalFileParserService implements FileExtractorServiceInterface {

    @Override
    public File getFileFromUrl(String path) throws MalformedURLException, CustomException {
        return new File(path);
    }
}
