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
    @DisplayName("TC-CALC-01: Thu nhập dưới ngưỡng giảm trừ bản thân (<= 11 triệu) -> Thuế = 0")
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
        TaxCalculationResult res = calculator.calculate(11000000.0, 0, 0, 0, 0, 0, config);
        assertEquals(0.0, res.getAssessedIncome());
        assertEquals(0.0, res.getTotalTax());
    }

    @Test
    @DisplayName("TC-CALC-03: Biên Bậc 1 - TNTT đúng bằng 5.000.000đ -> Thuế = 250.000đ (5%)")
    void testBracket1MaxBoundary() {
        double tax = calculator.calculateTaxOnly(5000000.0);
        assertEquals(250000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(5000000.0));
    }

    @Test
    @DisplayName("TC-CALC-04: Cận trên Bậc 1 chớm sang Bậc 2 - TNTT = 5.000.001đ")
    void testBracket1ToBracket2Boundary() {
        double tax = calculator.calculateTaxOnly(5000001.0);
        // Bậc 1: 5.000.000 * 5% = 250.000
        // Bậc 2: 1đ * 10% = 0đ (làm tròn)
        assertEquals(250000.0, tax, 1.0);
    }

    @Test
    @DisplayName("TC-CALC-05: Biên Bậc 2 - TNTT đúng bằng 10.000.000đ -> Thuế = 750.000đ")
    void testBracket2MaxBoundary() {
        // Bậc 1: 5tr * 5% = 250k
        // Bậc 2: 5tr * 10% = 500k
        // Tổng: 750k
        double tax = calculator.calculateTaxOnly(10000000.0);
        assertEquals(750000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(10000000.0));
    }

    @Test
    @DisplayName("TC-CALC-06: Biên Bậc 3 - TNTT đúng bằng 18.000.000đ -> Thuế = 1.950.000đ")
    void testBracket3MaxBoundary() {
        // Bậc 1: 250k, Bậc 2: 500k, Bậc 3: 8tr * 15% = 1.200k. Tổng: 1.950k
        double tax = calculator.calculateTaxOnly(18000000.0);
        assertEquals(1950000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(18000000.0));
    }

    @Test
    @DisplayName("TC-CALC-07: Biên Bậc 4 - TNTT đúng bằng 32.000.000đ -> Thuế = 4.750.000đ")
    void testBracket4MaxBoundary() {
        // Bậc 1-3: 1.950k, Bậc 4: 14tr * 20% = 2.800k. Tổng = 4.750k
        double tax = calculator.calculateTaxOnly(32000000.0);
        assertEquals(4750000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(32000000.0));
    }

    @Test
    @DisplayName("TC-CALC-08: Biên Bậc 5 - TNTT đúng bằng 52.000.000đ -> Thuế = 9.750.000đ")
    void testBracket5MaxBoundary() {
        // Bậc 1-4: 4.750k, Bậc 5: 20tr * 25% = 5.000k. Tổng = 9.750k
        double tax = calculator.calculateTaxOnly(52000000.0);
        assertEquals(9750000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(52000000.0));
    }

    @Test
    @DisplayName("TC-CALC-09: Biên Bậc 6 - TNTT đúng bằng 80.000.000đ -> Thuế = 18.150.000đ")
    void testBracket6MaxBoundary() {
        // Bậc 1-5: 9.750k, Bậc 6: 28tr * 30% = 8.400k. Tổng = 18.150k
        double tax = calculator.calculateTaxOnly(80000000.0);
        assertEquals(18150000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(80000000.0));
    }

    @Test
    @DisplayName("TC-CALC-10: Bậc 7 - TNTT = 120.000.000đ (> 80 triệu) -> Thuế = 32.150.000đ")
    void testBracket7() {
        // Bậc 1-6: 18.150k, Bậc 7: 40tr * 35% = 14.000k. Tổng = 32.150k
        // Công thức rút gọn: 120tr * 35% - 9.850.000 = 42.000.000 - 9.850.000 = 32.150.000đ
        double tax = calculator.calculateTaxOnly(120000000.0);
        assertEquals(32150000.0, tax, 1.0);
        assertEquals(tax, calculator.calculateQuickFormula(120000000.0));
    }

    @ParameterizedTest(name = "Đối chiếu 2 phương pháp với TNTT = {0}đ -> Kỳ vọng thuế = {1}đ")
    @CsvSource({
        "0, 0",
        "2000000, 100000",
        "5000000, 250000",
        "7000000, 450000",
        "10000000, 750000",
        "15000000, 1500000",
        "18000000, 1950000",
        "25000000, 3350000",
        "32000000, 4750000",
        "40000000, 6750000",
        "52000000, 9750000",
        "60000000, 12150000",
        "80000000, 18150000",
        "100000000, 25150000",
        "200000000, 60150000"
    })
    @DisplayName("TC-CALC-11: Kiểm thử bảng quyết định & đồng nhất giữa phương pháp Lũy tiến và Rút gọn")
    void testProgressiveAndQuickFormulaConsistency(double assessedIncome, double expectedTax) {
        double taxProg = calculator.calculateTaxOnly(assessedIncome);
        double taxQuick = calculator.calculateQuickFormula(assessedIncome);
        assertEquals(expectedTax, taxProg, 1.0, "Lũy tiến không khớp kỳ vọng");
        assertEquals(expectedTax, taxQuick, 1.0, "Công thức rút gọn không khớp kỳ vọng");
    }

    @Test
    @DisplayName("TC-CALC-12: Giảm trừ gia cảnh nhiều người phụ thuộc (2 người = 8.8tr)")
    void testDependentsDeduction() {
        TaxCalculationResult res = calculator.calculate(30000000.0, 0, 0, 0, 2, 0, config);
        assertEquals(8800000.0, res.getDependentDeduction());
        assertEquals(11000000.0, res.getPersonalDeduction());
        assertEquals(3150000.0, res.getTotalInsurance()); // 30tr * 10.5%
        // Tổng giảm trừ = 11tr + 8.8tr + 3.15tr = 22.95tr
        assertEquals(22950000.0, res.getTotalDeductions());
        // TNTT = 30tr - 22.95tr = 7.050.000đ (Bậc 2)
        assertEquals(7050000.0, res.getAssessedIncome());
        // Thuế = 5tr * 5% + 2.05tr * 10% = 250k + 205k = 455.000đ
        assertEquals(455000.0, res.getTotalTax(), 1.0);
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
