package com.codepeek.service.downloadFile.interfaces;

import com.codepeek.service.common.CustomException;

import java.io.File;

public interface DownloadFileServiceInterface {
    File getFileFromUrl(String path) throws CustomException;
}
