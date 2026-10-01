package com.pit.model;

import java.util.ArrayList;
import java.util.List;

public class TaxCalculationResult {
    private double grossSalary;              // Lương cơ bản/chính
    private double bonusIncome;              // Thưởng
    private double otherIncome;              // Thu nhập chịu thuế khác
    private double totalGrossIncome;         // Tổng thu nhập trước thuế
    private double nonTaxableIncome;         // Thu nhập miễn thuế
    private double taxableIncome;            // Thu nhập chịu thuế = Tổng thu nhập - Miễn thuế
    
    // Các khoản giảm trừ
    private double personalDeduction;        // Giảm trừ bản thân (11tr)
    private int dependentCount;              // Số người phụ thuộc
    private double dependentDeduction;       // Tổng giảm trừ người phụ thuộc
    private double insuranceBhxh;            // BHXH (8%)
    private double insuranceBhyt;            // BHYT (1.5%)
    private double insuranceBhtn;            // BHTN (1%)
    private double totalInsurance;           // Tổng bảo hiểm bắt buộc (10.5%)
    private double charityDonation;          // Đóng góp từ thiện, khuyến học
    private double totalDeductions;          // Tổng các khoản giảm trừ
    
    // Thu nhập tính thuế & Thuế
    private double assessedIncome;           // Thu nhập tính thuế = Thu nhập chịu thuế - Các khoản giảm trừ
    private double totalTax;                 // Tổng thuế TNCN phải nộp
    private double netIncome;                // Thu nhập thực nhận = Tổng thu nhập - Bảo hiểm - Thuế TNCN
    private String calculationMethod;        // "Lũy tiến từng phần (7 bậc)"
    
    // Chi tiết từng bậc thuế
    private List<BracketDetail> bracketDetails = new ArrayList<>();

    public static class BracketDetail {
        private int bracket;
        private String description;
        private double incomeInBracket;
        private double rate;
        private double taxAmount;

        public BracketDetail() {}

        public BracketDetail(int bracket, String description, double incomeInBracket, double rate, double taxAmount) {
            this.bracket = bracket;
            this.description = description;
            this.incomeInBracket = incomeInBracket;
            this.rate = rate;
            this.taxAmount = taxAmount;
        }

        public int getBracket() { return bracket; }
        public void setBracket(int bracket) { this.bracket = bracket; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public double getIncomeInBracket() { return incomeInBracket; }
        public void setIncomeInBracket(double incomeInBracket) { this.incomeInBracket = incomeInBracket; }

        public double getRate() { return rate; }
        public void setRate(double rate) { this.rate = rate; }

        public double getTaxAmount() { return taxAmount; }
        public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }
    }

    public TaxCalculationResult() {}

    // Getters and Setters
    public double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(double grossSalary) { this.grossSalary = grossSalary; }

    public double getBonusIncome() { return bonusIncome; }
    public void setBonusIncome(double bonusIncome) { this.bonusIncome = bonusIncome; }

    public double getOtherIncome() { return otherIncome; }
    public void setOtherIncome(double otherIncome) { this.otherIncome = otherIncome; }

    public double getTotalGrossIncome() { return totalGrossIncome; }
    public void setTotalGrossIncome(double totalGrossIncome) { this.totalGrossIncome = totalGrossIncome; }

    public double getNonTaxableIncome() { return nonTaxableIncome; }
    public void setNonTaxableIncome(double nonTaxableIncome) { this.nonTaxableIncome = nonTaxableIncome; }

    public double getTaxableIncome() { return taxableIncome; }
    public void setTaxableIncome(double taxableIncome) { this.taxableIncome = taxableIncome; }

    public double getPersonalDeduction() { return personalDeduction; }
    public void setPersonalDeduction(double personalDeduction) { this.personalDeduction = personalDeduction; }

    public int getDependentCount() { return dependentCount; }
    public void setDependentCount(int dependentCount) { this.dependentCount = dependentCount; }

    public double getDependentDeduction() { return dependentDeduction; }
    public void setDependentDeduction(double dependentDeduction) { this.dependentDeduction = dependentDeduction; }

    public double getInsuranceBhxh() { return insuranceBhxh; }
    public void setInsuranceBhxh(double insuranceBhxh) { this.insuranceBhxh = insuranceBhxh; }

    public double getInsuranceBhyt() { return insuranceBhyt; }
    public void setInsuranceBhyt(double insuranceBhyt) { this.insuranceBhyt = insuranceBhyt; }

    public double getInsuranceBhtn() { return insuranceBhtn; }
    public void setInsuranceBhtn(double insuranceBhtn) { this.insuranceBhtn = insuranceBhtn; }

    public double getTotalInsurance() { return totalInsurance; }
    public void setTotalInsurance(double totalInsurance) { this.totalInsurance = totalInsurance; }

    public double getCharityDonation() { return charityDonation; }
    public void setCharityDonation(double charityDonation) { this.charityDonation = charityDonation; }

    public double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(double totalDeductions) { this.totalDeductions = totalDeductions; }

    public double getAssessedIncome() { return assessedIncome; }
    public void setAssessedIncome(double assessedIncome) { this.assessedIncome = assessedIncome; }

    public double getTotalTax() { return totalTax; }
    public void setTotalTax(double totalTax) { this.totalTax = totalTax; }

    public double getNetIncome() { return netIncome; }
    public void setNetIncome(double netIncome) { this.netIncome = netIncome; }

    public String getCalculationMethod() { return calculationMethod; }
    public void setCalculationMethod(String calculationMethod) { this.calculationMethod = calculationMethod; }

    public List<BracketDetail> getBracketDetails() { return bracketDetails; }
    public void setBracketDetails(List<BracketDetail> bracketDetails) { this.bracketDetails = bracketDetails; }
}
