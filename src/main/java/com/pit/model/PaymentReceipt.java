package com.pit.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentReceipt {
    private String receiptId;
    private String declarationId;
    private String userId;
    private String fullName;
    private String taxCode;
    private double amount;
    private String paymentMethod; // "BANK_TRANSFER", "VNPAY_QR", "CREDIT_CARD"
    private String transactionRef;
    private String paymentTime;
    private String status; // "SUCCESS", "FAILED"

    public PaymentReceipt() {
        this.paymentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = "SUCCESS";
    }

    public PaymentReceipt(String receiptId, String declarationId, String userId, String fullName, 
                          String taxCode, double amount, String paymentMethod, String transactionRef) {
        this();
        this.receiptId = receiptId;
        this.declarationId = declarationId;
        this.userId = userId;
        this.fullName = fullName;
        this.taxCode = taxCode;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionRef = transactionRef;
    }

    public String getReceiptId() { return receiptId; }
    public void setReceiptId(String receiptId) { this.receiptId = receiptId; }

    public String getDeclarationId() { return declarationId; }
    public void setDeclarationId(String declarationId) { this.declarationId = declarationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getPaymentTime() { return paymentTime; }
    public void setPaymentTime(String paymentTime) { this.paymentTime = paymentTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
