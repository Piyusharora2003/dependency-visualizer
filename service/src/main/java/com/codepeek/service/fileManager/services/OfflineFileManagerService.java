package com.codepeek.service.fileManager.services;

import com.codepeek.service.fileManager.interfaces.FileManagerInterface;

import java.util.List;

public class OfflineFileManagerService implements FileManagerInterface {
    public List<String> getFilesAddress(String folderUrl) {
        return List.of();
    }
}
