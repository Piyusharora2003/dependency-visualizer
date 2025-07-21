package com.codepeek.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GraphEdge {
    private final String from;
    private final String to;
}