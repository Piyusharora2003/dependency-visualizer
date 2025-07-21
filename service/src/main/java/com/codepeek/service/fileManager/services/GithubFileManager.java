package com.codepeek.service.fileManager.services;

import com.codepeek.service.fileManager.interfaces.FileManagerInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Qualifier("gitFileManager")
@Service
public class GithubFileManager implements FileManagerInterface {
    @Override
    public List<String> getFilesAddress(String folderUrl) {
        return List.of();
    }
}
