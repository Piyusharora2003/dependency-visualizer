package com.codepeek.service.fileManager.services;

import com.codepeek.service.fileManager.interfaces.FileManagerInterface;
import com.codepeek.service.folderParser.model.FileInputData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Qualifier("gitFileManager")
@Service
@Slf4j
public class GithubFileManagerService implements FileManagerInterface {

    @Override
    public List<String> getFilesAddress(FileInputData fileInputData) {
        // form : https://github.com/microsoft/TypeScript
        try {
            String url = getRequiredGithubApiLink(fileInputData.getUrl());
            WebClient.Builder webClientBuilder = WebClient.builder()
                    .baseUrl(url)
                    .defaultHeader("User-Agent", "dependency-parser-app");
            if (!StringUtils.hasText(fileInputData.getGithubAccessToken())) {
                webClientBuilder.defaultHeader("Authorization", "Bearer YOUR_GITHUB_TOKEN");
            }
            WebClient webClient = webClientBuilder.build();
            return List.of();
        } catch (MalformedURLException malformedURLException) {
            log.error("Invalid github url received: {}", fileInputData.getUrl());
            return List.of();
        }
    }

    private String getRequiredGithubApiLink(String githubUrl) throws MalformedURLException {
        URL url = new URL(githubUrl);
        String[] pathSegments = url.getPath().split("/");

        // ✅ Extract owner and repo
        if (pathSegments.length < 3) {
            throw new IllegalArgumentException("Invalid GitHub URL: " + githubUrl);
        }

        String owner = pathSegments[1];
        String repo = pathSegments[2];

        return String.format(
                "https://api.github.com/repos/%s/%s/git/trees/main?recursive=1",
                owner, repo
        );
    }

}


// https://api.github.com/repos/Piyusharora2003/dependency-visualizer/git/trees/main?recursive=1