package com.codepeek.service.fileManager.services;

import com.codepeek.service.fileManager.interfaces.FileManagerInterface;
import com.codepeek.service.folderParser.model.FileInputData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Qualifier("localFileManager")
@Service
@Slf4j
public class OfflineFileManagerService implements FileManagerInterface {
    public List<String> getFilesAddress(FileInputData fileInputData) {
        File rootFolder = new File(fileInputData.getUrl());
        List<String> filesAddresses = new ArrayList<>();
        dfs(rootFolder, filesAddresses);
        return filesAddresses;
    }

    private void dfs(File currentDir, List<String> fileAddresses) {
        if (currentDir.isFile()) {
            fileAddresses.add(currentDir.getAbsolutePath());
            return;
        }
        Arrays.stream(Objects.requireNonNull(currentDir.listFiles())).forEach(file -> {
            dfs(file, fileAddresses);
        });
    }
}

