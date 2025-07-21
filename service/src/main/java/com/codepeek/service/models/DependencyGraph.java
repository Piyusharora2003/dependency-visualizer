package com.codepeek.service.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DependencyGraph {
    List<GraphNode> nodes;
    List<GraphEdge> edges;
}