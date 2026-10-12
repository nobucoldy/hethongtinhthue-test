package com.pit;

import com.pit.model.SystemConfig;
import com.pit.model.TaxCalculationResult;
import com.pit.service.TaxCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YC 6: Unit Test - Thuật toán tính thuế TNCN (TaxCalculatorService)")
public class TaxCalculatorServiceTest {

    private TaxCalculatorService calculator;
    private SystemConfig config;

    @BeforeEach
    void setUp() {
        calculator = new TaxCalculatorService();
        config = new SystemConfig();
    }

    @Test
    @DisplayName("TC-CALC-01: Thu nhập dưới ngưỡng giảm trừ bản thân (<= 15.5 triệu) -> Thuế = 0")
    void testIncomeBelowPersonalDeduction() {
        TaxCalculationResult res = calculator.calculate(10000000.0, 0, 0, 0, 0, 0, config);
        assertEquals(10000000.0, res.getTotalGrossIncome());
        assertEquals(1050000.0, res.getTotalInsurance()); // 10tr * 10.5%
        assertEquals(0.0, res.getAssessedIncome(), "TNTT phải bằng 0");
        assertEquals(0.0, res.getTotalTax(), "Thuế TNCN phải bằng 0");
        assertEquals(10000000.0 - 1050000.0, res.getNetIncome());
    }

    @Test
    @DisplayName("TC-CALC-02: Thu nhập đúng bằng mức giảm trừ bản thân và bảo hiểm -> Thuế = 0")
    void testExactDeductionThreshold() {
        // Thu nhập tính thuế = 0
        TaxCalculationResult res = calculator.calculate(15500000.0, 0, 0, 0, 0, 0, config);
        assertEquals(0.0, res.getAssessedIncome());
        assertEquals(0.0, res.getTotalTax());
    }

    @Test
    @DisplayName("TC-CALC-03: Biên Bậc 1 - TNTT đúng bằng 10.000.000đ -> Thuế = 500.000đ (5%)")
    void testBracket1MaxBoundary() {
        double tax = calculator.calculateTaxOnly(10000000.0);
        assertEquals(500000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(10000000.0));
    }

    @Test
    @DisplayName("TC-CALC-04: Cận trên Bậc 1 chớm sang Bậc 2 - TNTT = 10.000.001đ")
    void testBracket1ToBracket2Boundary() {
        double tax = calculator.calculateTaxOnly(10000001.0);
        // Bậc 1: 10.000.000 * 5% = 500.000
        // Bậc 2: 1đ * 10% = 0đ (làm tròn)
        assertEquals(500000.0, tax, 1.0);
    }

    @Test
    @DisplayName("TC-CALC-05: Biên Bậc 2 - TNTT đúng bằng 30.000.000đ -> Thuế = 2.500.000đ (10%)")
    void testBracket2MaxBoundary() {
        // Bậc 1: 10tr * 5% = 500k
        // Bậc 2: 20tr * 10% = 2.000k
        // Tổng: 2.500k. Rút gọn: 30tr * 10% - 500k = 2.500.000đ
        double tax = calculator.calculateTaxOnly(30000000.0);
        assertEquals(2500000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(30000000.0));
    }

    @Test
    @DisplayName("TC-CALC-06: Biên Bậc 3 - TNTT đúng bằng 60.000.000đ -> Thuế = 8.500.000đ (20%)")
    void testBracket3MaxBoundary() {
        // Bậc 1-2: 2.500k, Bậc 3: 30tr * 20% = 6.000k. Tổng: 8.500.000đ
        // Rút gọn: 60tr * 20% - 3.500.000đ = 8.500.000đ
        double tax = calculator.calculateTaxOnly(60000000.0);
        assertEquals(8500000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(60000000.0));
    }

    @Test
    @DisplayName("TC-CALC-07: Biên Bậc 4 - TNTT đúng bằng 100.000.000đ -> Thuế = 20.500.000đ (30%)")
    void testBracket4MaxBoundary() {
        // Bậc 1-3: 8.500k, Bậc 4: 40tr * 30% = 12.000k. Tổng = 20.500.000đ
        // Rút gọn: 100tr * 30% - 9.500.000đ = 20.500.000đ
        double tax = calculator.calculateTaxOnly(100000000.0);
        assertEquals(20500000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(100000000.0));
    }

    @Test
    @DisplayName("TC-CALC-08: Bậc 5 - TNTT = 120.000.000đ (> 100 triệu) -> Thuế = 27.500.000đ (35%)")
    void testBracket5() {
        // Bậc 1-4: 20.500k, Bậc 5: 20tr * 35% = 7.000k. Tổng = 27.500.000đ
        // Công thức rút gọn: 120tr * 35% - 14.500.000đ = 42tr - 14.5tr = 27.500.000đ
        double tax = calculator.calculateTaxOnly(120000000.0);
        assertEquals(27500000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(120000000.0));
    }

    @Test
    @DisplayName("TC-CALC-09: Bậc 5 mức cao - TNTT = 150.000.000đ -> Thuế = 38.000.000đ")
    void testBracket5High() {
        // Bậc 1-4: 20.500k, Bậc 5: 50tr * 35% = 17.500k. Tổng = 38.000.000đ
        // Rút gọn: 150tr * 35% - 14.500.000đ = 52.5tr - 14.5tr = 38.000.000đ
        double tax = calculator.calculateTaxOnly(150000000.0);
        assertEquals(38000000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(150000000.0));
    }

    @ParameterizedTest(name = "Đối chiếu 2 phương pháp với TNTT = {0}đ -> Kỳ vọng thuế = {1}đ")
    @CsvSource({
        "0, 0",
        "5000000, 250000",
        "10000000, 500000",
        "15000000, 1000000",
        "20000000, 1500000",
        "30000000, 2500000",
        "40000000, 4500000",
        "50000000, 6500000",
        "60000000, 8500000",
        "80000000, 14500000",
        "100000000, 20500000",
        "120000000, 27500000",
        "150000000, 38000000",
        "200000000, 55500000"
    })
    @DisplayName("TC-CALC-10: Kiểm thử bảng quyết định & đồng nhất giữa phương pháp Lũy tiến và Rút gọn (5 Bậc)")
    void testProgressiveAndQuickFormulaConsistency(double assessedIncome, double expectedTax) {
        double taxProg = calculator.calculateTaxOnly(assessedIncome);
        double taxQuick = calculator.calculateQuickFormula(assessedIncome);
        assertEquals(expectedTax, taxProg, 1.0, "Lũy tiến không khớp kỳ vọng");
        assertEquals(expectedTax, taxQuick, 1.0, "Công thức rút gọn không khớp kỳ vọng");
    }

    @Test
    @DisplayName("TC-CALC-11: Giảm trừ gia cảnh nhiều người phụ thuộc (2 người = 12.4tr, bản thân = 15.5tr)")
    void testDependentsDeduction() {
        TaxCalculationResult res = calculator.calculate(40000000.0, 0, 0, 0, 2, 0, config);
        assertEquals(12400000.0, res.getDependentDeduction());
        assertEquals(15500000.0, res.getPersonalDeduction());
        assertEquals(4200000.0, res.getTotalInsurance()); // 40tr * 10.5%
        // Tổng giảm trừ = 15.5tr + 12.4tr + 4.2tr = 32.1tr
        assertEquals(32100000.0, res.getTotalDeductions());
        // TNTT = 40tr - 32.1tr = 7.900.000đ (Bậc 1 <= 10m)
        assertEquals(7900000.0, res.getAssessedIncome());
        // Thuế = 7.9tr * 5% = 395.000đ
        assertEquals(395000.0, res.getTotalTax(), 1.0);
    }

    @Test
    @DisplayName("TC-CALC-12: Ví dụ thực tế từ báo Chính Phủ: Lương 35 triệu, 1 người phụ thuộc -> Thuế = 481.250đ")
    void testGovernmentRealWorldExample() {
        TaxCalculationResult res = calculator.calculate(35000000.0, 0, 0, 0, 1, 0, config);
        // BH bắt buộc (10.5%): 3.675.000đ
        assertEquals(3675000.0, res.getTotalInsurance());
        // Giảm trừ bản thân: 15.500.000đ
        assertEquals(15500000.0, res.getPersonalDeduction());
        // Giảm trừ 1 NPT: 6.200.000đ
        assertEquals(6200000.0, res.getDependentDeduction());
        // Tổng giảm trừ: 25.375.000đ
        assertEquals(25375000.0, res.getTotalDeductions());
        // TNTT: 35.000.000 - 25.375.000 = 9.625.000đ (Bậc 1 <= 10m)
        assertEquals(9625000.0, res.getAssessedIncome());
        // Thuế TNCN: 9.625.000 * 5% = 481.250đ
        assertEquals(481250.0, res.getTotalTax(), 1.0);
    }

    @Test
    @DisplayName("TC-CALC-13: Lương vượt trần đóng bảo hiểm (VD: 80 triệu, trần 46.8 triệu)")
    void testInsuranceSalaryCap() {
        TaxCalculationResult res = calculator.calculate(80000000.0, 0, 0, 0, 0, 0, config);
        double expectedInsurance = Math.round(46800000.0 * 0.105);
        assertEquals(expectedInsurance, res.getTotalInsurance());
    }

    @Test
    @DisplayName("TC-CALC-14: Xử lý dữ liệu âm và trường hợp null config")
    void testNegativeInputsAndNullConfig() {
        TaxCalculationResult res = calculator.calculate(-1000.0, -500.0, -100.0, -50.0, -2, -10.0, null);
        assertNotNull(res);
        assertEquals(0.0, res.getTotalGrossIncome());
        assertEquals(0.0, res.getTotalTax());
    }

    @Test
    @DisplayName("TC-CALC-15: Đóng góp từ thiện nhân đạo được trừ vào thu nhập chịu thuế")
    void testCharityDonationDeduction() {
        TaxCalculationResult resWithoutCharity = calculator.calculate(25000000.0, 0, 0, 0, 0, 0, config);
        TaxCalculationResult resWithCharity = calculator.calculate(25000000.0, 0, 0, 0, 0, 2000000.0, config);

        assertEquals(resWithoutCharity.getAssessedIncome() - 2000000.0, resWithCharity.getAssessedIncome());
        assertTrue(resWithCharity.getTotalTax() < resWithoutCharity.getTotalTax());
    }
}
