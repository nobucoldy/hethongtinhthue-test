package com.pit.service;

import com.pit.model.SystemConfig;
import com.pit.model.TaxBracket;
import com.pit.model.TaxCalculationResult;
import com.pit.model.TaxCalculationResult.BracketDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaxCalculatorService {

    /**
     * Tính toán toàn bộ thuế TNCN từ các khoản thu nhập và giảm trừ
     */
    public TaxCalculationResult calculate(double grossSalary, double bonusIncome, double otherIncome,
                                          double nonTaxableIncome, int dependentCount, double charityDonation,
                                          SystemConfig config) {
        if (config == null) {
            config = new SystemConfig();
        }

        // Đảm bảo không có giá trị âm bất thường
        grossSalary = Math.max(0, grossSalary);
        bonusIncome = Math.max(0, bonusIncome);
        otherIncome = Math.max(0, otherIncome);
        nonTaxableIncome = Math.max(0, nonTaxableIncome);
        dependentCount = Math.max(0, dependentCount);
        charityDonation = Math.max(0, charityDonation);

        TaxCalculationResult result = new TaxCalculationResult();
        result.setGrossSalary(grossSalary);
        result.setBonusIncome(bonusIncome);
        result.setOtherIncome(otherIncome);
        result.setNonTaxableIncome(nonTaxableIncome);
        result.setDependentCount(dependentCount);
        result.setCharityDonation(charityDonation);

        // 1. Tổng thu nhập
        double totalGross = grossSalary + bonusIncome + otherIncome;
        result.setTotalGrossIncome(totalGross);

        // 2. Thu nhập chịu thuế (TNCT) = Tổng thu nhập - Thu nhập miễn thuế
        double taxableIncome = Math.max(0, totalGross - nonTaxableIncome);
        result.setTaxableIncome(taxableIncome);

        // 3. Các khoản bảo hiểm bắt buộc (tính trên lương chính, có trần bảo hiểm)
        double insuranceBase = Math.min(grossSalary, config.getInsuranceBaseSalaryCap());
        double bhxh = Math.round(insuranceBase * config.getBhxhRate());
        double bhyt = Math.round(insuranceBase * config.getBhytRate());
        double bhtn = Math.round(insuranceBase * config.getBhtnRate());
        double totalInsurance = bhxh + bhyt + bhtn;

        result.setInsuranceBhxh(bhxh);
        result.setInsuranceBhyt(bhyt);
        result.setInsuranceBhtn(bhtn);
        result.setTotalInsurance(totalInsurance);

        // 4. Các khoản giảm trừ gia cảnh
        double personalDeduction = config.getPersonalDeduction();
        double dependentDeduction = dependentCount * config.getDependentDeduction();
        double totalDeductions = personalDeduction + dependentDeduction + totalInsurance + charityDonation;

        result.setPersonalDeduction(personalDeduction);
        result.setDependentDeduction(dependentDeduction);
        result.setTotalDeductions(totalDeductions);

        // 5. Thu nhập tính thuế (TNTT) = TNCT - Các khoản giảm trừ
        double assessedIncome = Math.max(0, taxableIncome - totalDeductions);
        result.setAssessedIncome(assessedIncome);

        // 6. Tính thuế lũy tiến từng phần 7 bậc
        List<BracketDetail> details = new ArrayList<>();
        double totalTax = 0.0;

        List<TaxBracket> brackets = config.getBrackets();
        if (brackets == null || brackets.isEmpty()) {
            brackets = SystemConfig.initDefaultBrackets();
        }

        if (assessedIncome > 0) {
            for (TaxBracket b : brackets) {
                if (assessedIncome > b.getMinIncome()) {
                    double incomeInThisBracket = Math.min(assessedIncome, b.getMaxIncome()) - b.getMinIncome();
                    double taxForThisBracket = Math.round(incomeInThisBracket * b.getTaxRate());
                    totalTax += taxForThisBracket;

                    details.add(new BracketDetail(
                            b.getBracketOrder(),
                            b.getDescription(),
                            incomeInThisBracket,
                            b.getTaxRate(),
                            taxForThisBracket
                    ));
                } else {
                    details.add(new BracketDetail(
                            b.getBracketOrder(),
                            b.getDescription(),
                            0.0,
                            b.getTaxRate(),
                            0.0
                    ));
                }
            }
        } else {
            for (TaxBracket b : brackets) {
                details.add(new BracketDetail(
                        b.getBracketOrder(),
                        b.getDescription(),
                        0.0,
                        b.getTaxRate(),
                        0.0
                ));
            }
        }

        result.setBracketDetails(details);
        result.setTotalTax(totalTax);
        result.setNetIncome(totalGross - totalInsurance - totalTax);
        result.setCalculationMethod("Biểu thuế lũy tiến từng phần (7 bậc)");

        return result;
    }

    /**
     * Tính thuế trực tiếp từ Thu nhập tính thuế (TNTT) theo biểu lũy tiến
     */
    public double calculateTaxOnly(double assessedIncome) {
        if (assessedIncome <= 0) return 0.0;
        List<TaxBracket> brackets = SystemConfig.initDefaultBrackets();
        double totalTax = 0.0;
        for (TaxBracket b : brackets) {
            if (assessedIncome > b.getMinIncome()) {
                double incomeInThisBracket = Math.min(assessedIncome, b.getMaxIncome()) - b.getMinIncome();
                totalTax += Math.round(incomeInThisBracket * b.getTaxRate());
            }
        }
        return totalTax;
    }

    /**
     * Tính thuế bằng công thức rút gọn để đối chiếu kiểm tra
     */
    public double calculateQuickFormula(double assessedIncome) {
        if (assessedIncome <= 0) return 0.0;
        if (assessedIncome <= 5000000.0) {
            return Math.round(assessedIncome * 0.05);
        } else if (assessedIncome <= 10000000.0) {
            return Math.round(assessedIncome * 0.10 - 250000.0);
        } else if (assessedIncome <= 18000000.0) {
            return Math.round(assessedIncome * 0.15 - 750000.0);
        } else if (assessedIncome <= 32000000.0) {
            return Math.round(assessedIncome * 0.20 - 1650000.0);
        } else if (assessedIncome <= 52000000.0) {
            return Math.round(assessedIncome * 0.25 - 3250000.0);
        } else if (assessedIncome <= 80000000.0) {
            return Math.round(assessedIncome * 0.30 - 5850000.0);
        } else {
            return Math.round(assessedIncome * 0.35 - 9850000.0);
        }
    }
}
