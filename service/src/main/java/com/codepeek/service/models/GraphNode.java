package com.codepeek.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GraphNode {
    private final String id; // e.g., "OrderService.createOrder"
    private final String label; // e.g., "createOrder"
    private final String group; // e.g., "OrderService"
}