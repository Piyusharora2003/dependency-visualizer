package com.codepeek.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private final InventoryService inventoryService;

    @Autowired
    private final NotificationService notificationService;

    OrderService(InventoryService inventoryService, NotificationService notificationService) {
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
    }

    public boolean createOrder(String productId, int quantity) {
        // Check if the product is in stock
        if (!inventoryService.isStockAvailable(productId, quantity)) {
            System.out.println("Product " + productId + " is out of stock.");
            return false;
        }

        // If available, update the stock
        inventoryService.updateStock(productId, -quantity);

        // Send a confirmation email
        String userEmail = "customer@example.com";
        String message = "Your order for product " + productId + " has been confirmed.";
        notificationService.sendEmail(userEmail, message);

        return true;
    }

    public void cancelOrder(String productId, int quantity) {
        // Restore the stock
        inventoryService.updateStock(productId, quantity);

        // Send a cancellation notification
        String userEmail = "customer@example.com";
        String message = "Your order for product " + productId + " has been cancelled.";
        notificationService.sendEmail(userEmail, message);
    }
}

@Service
class InventoryService {

    private final Map<String, Integer> stockLevels = new HashMap<>();

    public InventoryService() {
        // Initialize with some dummy data
        stockLevels.put("PROD123", 100);
        stockLevels.put("PROD456", 50);
    }

    public boolean isStockAvailable(String productId, int quantity) {
        return stockLevels.getOrDefault(productId, 0) >= quantity;
    }

    public void updateStock(String productId, int quantityChange) {
        int currentStock = stockLevels.getOrDefault(productId, 0);
        stockLevels.put(productId, currentStock + quantityChange);
        System.out.println("Stock for " + productId + " updated to " + stockLevels.get(productId));
    }
}

@Service
class NotificationService {

    public void sendEmail(String toAddress, String message) {
        // Dummy implementation of an email sender
        System.out.println("--- Sending Email ---");
        System.out.println("To: " + toAddress);
        System.out.println("Message: " + message);
        System.out.println("---------------------");
    }
}