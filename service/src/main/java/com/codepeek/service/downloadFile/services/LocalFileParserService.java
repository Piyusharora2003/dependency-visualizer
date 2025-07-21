package com.codepeek.service.downloadFile.services;

import com.codepeek.service.common.CustomException;
import com.codepeek.service.downloadFile.interfaces.DownloadFileServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
@Qualifier("localFileParser")
public class LocalFileParserService implements DownloadFileServiceInterface {

    @Override
    public File getFileFromUrl(String path) throws CustomException {
        return new File(path);
    }
}
