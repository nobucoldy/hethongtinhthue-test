package com.pit.model;

public class TaxBracket {
    private int bracketOrder;       // Bậc 1, 2, ..., 7
    private double minIncome;       // Ngưỡng dưới thu nhập tính thuế
    private double maxIncome;       // Ngưỡng trên thu nhập tính thuế
    private double taxRate;         // Thuế suất (0.05, 0.10, ..., 0.35)
    private double quickSubtract;   // Số tiền trừ theo công thức rút gọn
    private String description;     // Mô tả bậc thuế

    public TaxBracket() {}

    public TaxBracket(int bracketOrder, double minIncome, double maxIncome, double taxRate, double quickSubtract, String description) {
        this.bracketOrder = bracketOrder;
        this.minIncome = minIncome;
        this.maxIncome = maxIncome;
        this.taxRate = taxRate;
        this.quickSubtract = quickSubtract;
        this.description = description;
    }

    public int getBracketOrder() { return bracketOrder; }
    public void setBracketOrder(int bracketOrder) { this.bracketOrder = bracketOrder; }

    public double getMinIncome() { return minIncome; }
    public void setMinIncome(double minIncome) { this.minIncome = minIncome; }

    public double getMaxIncome() { return maxIncome; }
    public void setMaxIncome(double maxIncome) { this.maxIncome = maxIncome; }

    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    public double getQuickSubtract() { return quickSubtract; }
    public void setQuickSubtract(double quickSubtract) { this.quickSubtract = quickSubtract; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
