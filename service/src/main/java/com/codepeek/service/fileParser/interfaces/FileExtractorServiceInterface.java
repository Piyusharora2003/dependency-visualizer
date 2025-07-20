package com.codepeek.service.fileParser.interfaces;

import com.codepeek.service.common.CustomException;

import java.io.File;
import java.net.MalformedURLException;

public interface FileExtractorServiceInterface {
    File getFileFromUrl(String path) throws MalformedURLException, CustomException;
}
