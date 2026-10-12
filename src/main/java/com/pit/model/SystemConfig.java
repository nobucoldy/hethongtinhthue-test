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
        this.personalDeduction = 15500000.0; // 15.5 triệu đồng/tháng (Văn bản 112/VBHN-VPQH)
        this.dependentDeduction = 6200000.0;  // 6.2 triệu đồng/tháng/người (Văn bản 112/VBHN-VPQH)
        this.bhxhRate = 0.08;
        this.bhytRate = 0.015;
        this.bhtnRate = 0.01;
        this.insuranceBaseSalaryCap = 46800000.0; // 20 lần mức lương cơ sở hiện hành 2.340.000đ
        this.brackets = initDefaultBrackets();
    }

    public static List<TaxBracket> initDefaultBrackets() {
        List<TaxBracket> list = new ArrayList<>();
        list.add(new TaxBracket(1, 0.0, 10000000.0, 0.05, 0.0, "Đến 10 triệu VNĐ"));
        list.add(new TaxBracket(2, 10000000.0, 30000000.0, 0.10, 500000.0, "Trên 10 đến 30 triệu VNĐ"));
        list.add(new TaxBracket(3, 30000000.0, 60000000.0, 0.20, 3500000.0, "Trên 30 đến 60 triệu VNĐ"));
        list.add(new TaxBracket(4, 60000000.0, 100000000.0, 0.30, 9500000.0, "Trên 60 đến 100 triệu VNĐ"));
        list.add(new TaxBracket(5, 100000000.0, Double.MAX_VALUE, 0.35, 14500000.0, "Trên 100 triệu VNĐ"));
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
