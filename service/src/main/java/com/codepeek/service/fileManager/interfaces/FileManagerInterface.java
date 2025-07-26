package com.codepeek.service.fileManager.interfaces;

import com.codepeek.service.folderParser.model.FileInputData;

import java.util.List;

public interface FileManagerInterface {
    List<String> getFilesAddress(FileInputData fileInputData);
}
