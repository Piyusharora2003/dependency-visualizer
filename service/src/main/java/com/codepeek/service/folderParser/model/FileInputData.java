package com.codepeek.service.folderParser.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileInputData {
    private String url;
    private String urlType;
    private String githubAccessToken;
}
