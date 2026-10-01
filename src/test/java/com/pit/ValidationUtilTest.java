package com.pit;

import com.pit.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YC 6: Unit Test - Kiểm tra tính hợp lệ dữ liệu (ValidationUtil)")
public class ValidationUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {"user1", "admin_01", "nguyenvana", "abc"})
    @DisplayName("TC-VAL-01: Username hợp lệ (3-30 ký tự)")
    void testValidUsername(String username) {
        assertTrue(ValidationUtil.isValidUsername(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "", "   ", "user@123", "this_is_a_very_long_username_exceeding_30_characters_limit"})
    @DisplayName("TC-VAL-02: Username không hợp lệ (quá ngắn, quá dài, ký tự đặc biệt)")
    void testInvalidUsername(String username) {
        assertFalse(ValidationUtil.isValidUsername(username));
    }

    @Test
    @DisplayName("TC-VAL-03: Mật khẩu tối thiểu 6 ký tự")
    void testPasswordValidation() {
        assertTrue(ValidationUtil.isValidPassword("123456"));
        assertTrue(ValidationUtil.isValidPassword("Admin@123"));
        assertFalse(ValidationUtil.isValidPassword("12345"));
        assertFalse(ValidationUtil.isValidPassword(""));
        assertFalse(ValidationUtil.isValidPassword(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@example.com", "user.name@domain.vn", "contact+test@gdt.gov.vn"})
    @DisplayName("TC-VAL-04: Email hợp lệ")
    void testValidEmail(String email) {
        assertTrue(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"plainaddress", "@missinguser.com", "user@.com", "user@domain..com", ""})
    @DisplayName("TC-VAL-05: Email không hợp lệ")
    void testInvalidEmail(String email) {
        assertFalse(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0912345678", "0901234567", "0381234567", "0861234567"})
    @DisplayName("TC-VAL-06: Số điện thoại Việt Nam hợp lệ (10 số bắt đầu bằng 0)")
    void testValidPhone(String phone) {
        assertTrue(ValidationUtil.isValidPhone(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {"091234567", "09123456789", "1234567890", "091234567a", ""})
    @DisplayName("TC-VAL-07: Số điện thoại không hợp lệ")
    void testInvalidPhone(String phone) {
        assertFalse(ValidationUtil.isValidPhone(phone));
    }

    @Test
    @DisplayName("TC-VAL-08: Mã số thuế cá nhân hợp lệ (10 số)")
    void testTaxCodeValidation() {
        assertTrue(ValidationUtil.isValidTaxCode("0123456789"));
        assertTrue(ValidationUtil.isValidTaxCode("8012345678"));
        assertFalse(ValidationUtil.isValidTaxCode("123456789")); // 9 số
        assertFalse(ValidationUtil.isValidTaxCode("12345678901")); // 11 số
        assertFalse(ValidationUtil.isValidTaxCode("012345678a"));
    }

    @Test
    @DisplayName("TC-VAL-09: Căn cước công dân CCCD (12 số)")
    void testCitizenIdValidation() {
        assertTrue(ValidationUtil.isValidCitizenId("001200012345"));
        assertFalse(ValidationUtil.isValidCitizenId("00120001234")); // 11 số
        assertFalse(ValidationUtil.isValidCitizenId("0012000123456")); // 13 số
        assertFalse(ValidationUtil.isValidCitizenId("00120001234x"));
    }

    @Test
    @DisplayName("TC-VAL-10: Kiểm tra thu nhập không âm và trong giới hạn")
    void testIncomeValidation() {
        assertTrue(ValidationUtil.isValidIncome(0.0));
        assertTrue(ValidationUtil.isValidIncome(50000000.0));
        assertFalse(ValidationUtil.isValidIncome(-1.0));
        assertFalse(ValidationUtil.isValidIncome(Double.NaN));
        assertFalse(ValidationUtil.isValidIncome(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("TC-VAL-11: Số người phụ thuộc từ 0 đến 50")
    void testDependentCountValidation() {
        assertTrue(ValidationUtil.isValidDependentCount(0));
        assertTrue(ValidationUtil.isValidDependentCount(5));
        assertTrue(ValidationUtil.isValidDependentCount(50));
        assertFalse(ValidationUtil.isValidDependentCount(-1));
        assertFalse(ValidationUtil.isValidDependentCount(51));
    }

    @Test
    @DisplayName("TC-VAL-12: Kỳ tính thuế (Năm và Tháng)")
    void testTaxPeriodValidation() {
        assertTrue(ValidationUtil.isValidTaxPeriod(2026, 1));
        assertTrue(ValidationUtil.isValidTaxPeriod(2026, 12));
        assertTrue(ValidationUtil.isValidTaxPeriod(2026, 0)); // Quyết toán năm
        assertFalse(ValidationUtil.isValidTaxPeriod(1999, 5));
        assertFalse(ValidationUtil.isValidTaxPeriod(2101, 5));
        assertFalse(ValidationUtil.isValidTaxPeriod(2026, -1));
        assertFalse(ValidationUtil.isValidTaxPeriod(2026, 13));
    }
}
