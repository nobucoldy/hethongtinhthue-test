package com.pit.model;

import java.util.ArrayList;
import java.util.List;

public class SystemConfig {
    private double personalDeduction;       // Mức giảm trừ bản thân (VNĐ/tháng), mặc định 11.000.000
    private double dependentDeduction;      // Mức giảm trừ người phụ thuộc (VNĐ/tháng/người), mặc định 4.400.000
    private double bhxhRate;                 // Tỷ lệ BHXH (mặc định 0.08 = 8%)
    private double bhytRate;                 // Tỷ lệ BHYT (mặc định 0.015 = 1.5%)
    private double bhtnRate;                 // Tỷ lệ BHTN (mặc định 0.01 = 1%)
    private double insuranceBaseSalaryCap;   // Mức trần lương tính bảo hiểm (VNĐ), mặc định 36.000.000
    private List<TaxBracket> brackets;       // 7 bậc thuế lũy tiến từng phần

    public SystemConfig() {
        this.personalDeduction = 11000000.0;
        this.dependentDeduction = 4400000.0;
        this.bhxhRate = 0.08;
        this.bhytRate = 0.015;
        this.bhtnRate = 0.01;
        this.insuranceBaseSalaryCap = 46800000.0; // 20 lần mức lương cơ sở hiện hành 2.340.000đ
        this.brackets = initDefaultBrackets();
    }

    public static List<TaxBracket> initDefaultBrackets() {
        List<TaxBracket> list = new ArrayList<>();
        list.add(new TaxBracket(1, 0.0, 5000000.0, 0.05, 0.0, "Đến 5 triệu VNĐ"));
        list.add(new TaxBracket(2, 5000000.0, 10000000.0, 0.10, 250000.0, "Trên 5 đến 10 triệu VNĐ"));
        list.add(new TaxBracket(3, 10000000.0, 18000000.0, 0.15, 750000.0, "Trên 10 đến 18 triệu VNĐ"));
        list.add(new TaxBracket(4, 18000000.0, 32000000.0, 0.20, 1650000.0, "Trên 18 đến 32 triệu VNĐ"));
        list.add(new TaxBracket(5, 32000000.0, 52000000.0, 0.25, 3250000.0, "Trên 32 đến 52 triệu VNĐ"));
        list.add(new TaxBracket(6, 52000000.0, 80000000.0, 0.30, 5850000.0, "Trên 52 đến 80 triệu VNĐ"));
        list.add(new TaxBracket(7, 80000000.0, Double.MAX_VALUE, 0.35, 9850000.0, "Trên 80 triệu VNĐ"));
        return list;
    }

    public double getPersonalDeduction() { return personalDeduction; }
    public void setPersonalDeduction(double personalDeduction) { this.personalDeduction = personalDeduction; }

    public double getDependentDeduction() { return dependentDeduction; }
    public void setDependentDeduction(double dependentDeduction) { this.dependentDeduction = dependentDeduction; }

    public double getBhxhRate() { return bhxhRate; }
    public void setBhxhRate(double bhxhRate) { this.bhxhRate = bhxhRate; }

    public double getBhytRate() { return bhytRate; }
    public void setBhytRate(double bhytRate) { this.bhytRate = bhytRate; }

    public double getBhtnRate() { return bhtnRate; }
    public void setBhtnRate(double bhtnRate) { this.bhtnRate = bhtnRate; }

    public double getInsuranceBaseSalaryCap() { return insuranceBaseSalaryCap; }
    public void setInsuranceBaseSalaryCap(double insuranceBaseSalaryCap) { this.insuranceBaseSalaryCap = insuranceBaseSalaryCap; }

    public List<TaxBracket> getBrackets() { return brackets; }
    public void setBrackets(List<TaxBracket> brackets) { this.brackets = brackets; }
}
