package com.codepeek.service.fileParser.service;

import com.codepeek.service.common.CustomException;
import com.codepeek.service.fileParser.interfaces.FileExtractorServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

@Service
@Slf4j
public class GithubFileExtractorService implements FileExtractorServiceInterface {
    public File getFileFromUrl(String githubUrl) throws CustomException {
        if (githubUrl.contains("github.com") && githubUrl.contains("/blob/")) {
            githubUrl = githubUrl.replace("github.com", "raw.githubusercontent.com").replace("/blob/", "/");
        }
        try {
            URL url = new URL(githubUrl);
            InputStream in = url.openStream();
            File tempFile = Files.createTempFile("github-download-", ".tmp").toFile();
            try (OutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                throw new CustomException("error getting file from given link");
            } finally {
                in.close();
            }
            return tempFile;
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }
}
