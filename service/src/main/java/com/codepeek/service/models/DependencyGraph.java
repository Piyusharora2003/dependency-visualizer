package com.codepeek.service.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DependencyGraph {
    List<GraphNode> nodes;
    List<GraphEdge> edges;

    public DependencyGraph() {
        this.setNodes(new ArrayList<>());
        this.setEdges(new ArrayList<>());
    }
}