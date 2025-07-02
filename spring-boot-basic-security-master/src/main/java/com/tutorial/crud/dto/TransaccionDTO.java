package com.tutorial.crud.dto;

public class TransaccionDTO {
    private String buyOrder;
    private String sessionId;
    private Double amount;
    private String returnUrl;
    
    // Constructor vacío
    public TransaccionDTO() {}
    
    // Constructor con parámetros
    public TransaccionDTO(String buyOrder, String sessionId, Double amount, String returnUrl) {
        this.buyOrder = buyOrder;
        this.sessionId = sessionId;
        this.amount = amount;
        this.returnUrl = returnUrl;
    }
    
    // Getters y Setters
    public String getBuyOrder() {
        return buyOrder;
    }
    
    public void setBuyOrder(String buyOrder) {
        this.buyOrder = buyOrder;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}