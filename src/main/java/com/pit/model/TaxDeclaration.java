package com.pit.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaxDeclaration {
    private String id;
    private String userId;
    private String fullName;
    private String taxCode;
    private String citizenId;
    private int taxYear;
    private int taxMonth; // 1-12, hoặc 0 nếu quyết toán năm
    private double grossSalary;
    private double bonusIncome;
    private double otherIncome;
    private double nonTaxableIncome;
    private int dependentCount;
    private double charityDonation;
    
    // Kết quả tính toán
    private double totalGrossIncome;
    private double taxableIncome;
    private double totalDeductions;
    private double assessedIncome;
    private double taxAmount;
    private double netIncome;
    
    // Trạng thái: "PENDING" (Chờ nộp thuế), "PAID" (Đã nộp), "APPROVED" (Đã duyệt)
    private String status;
    private String createdAt;
    private String paymentReceiptId;

    public TaxDeclaration() {
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = "PENDING";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }

    public int getTaxYear() { return taxYear; }
    public void setTaxYear(int taxYear) { this.taxYear = taxYear; }

    public int getTaxMonth() { return taxMonth; }
    public void setTaxMonth(int taxMonth) { this.taxMonth = taxMonth; }

    public double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(double grossSalary) { this.grossSalary = grossSalary; }

    public double getBonusIncome() { return bonusIncome; }
    public void setBonusIncome(double bonusIncome) { this.bonusIncome = bonusIncome; }

    public double getOtherIncome() { return otherIncome; }
    public void setOtherIncome(double otherIncome) { this.otherIncome = otherIncome; }

    public double getNonTaxableIncome() { return nonTaxableIncome; }
    public void setNonTaxableIncome(double nonTaxableIncome) { this.nonTaxableIncome = nonTaxableIncome; }

    public int getDependentCount() { return dependentCount; }
    public void setDependentCount(int dependentCount) { this.dependentCount = dependentCount; }

    public double getCharityDonation() { return charityDonation; }
    public void setCharityDonation(double charityDonation) { this.charityDonation = charityDonation; }

    public double getTotalGrossIncome() { return totalGrossIncome; }
    public void setTotalGrossIncome(double totalGrossIncome) { this.totalGrossIncome = totalGrossIncome; }

    public double getTaxableIncome() { return taxableIncome; }
    public void setTaxableIncome(double taxableIncome) { this.taxableIncome = taxableIncome; }

    public double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(double totalDeductions) { this.totalDeductions = totalDeductions; }

    public double getAssessedIncome() { return assessedIncome; }
    public void setAssessedIncome(double assessedIncome) { this.assessedIncome = assessedIncome; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getNetIncome() { return netIncome; }
    public void setNetIncome(double netIncome) { this.netIncome = netIncome; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getPaymentReceiptId() { return paymentReceiptId; }
    public void setPaymentReceiptId(String paymentReceiptId) { this.paymentReceiptId = paymentReceiptId; }
}
