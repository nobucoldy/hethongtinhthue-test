package com.pit;

import com.pit.model.TaxDeclaration;
import com.pit.model.User;
import com.pit.service.ConfigService;
import com.pit.service.DeclarationService;
import com.pit.service.TaxCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YC 6: Unit Test - Quản lý tờ khai & Báo cáo (DeclarationService)")
public class DeclarationServiceTest {

    private DeclarationService declarationService;
    private TaxCalculatorService calculatorService;
    private ConfigService configService;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        calculatorService = new TaxCalculatorService();
        configService = new ConfigService();
        declarationService = new DeclarationService(calculatorService, configService);

        sampleUser = new User("u-test-99", "testuser", "Pass@123", "Nguyễn Kiểm Thử",
                "test@gmail.com", "0909090909", "1234567890", "001200099999", "TAXPAYER");
    }

    @Test
    @DisplayName("TC-DEC-01: Tạo tờ khai thuế thành công và tự động tính số thuế")
    void testCreateDeclaration() {
        TaxDeclaration dec = declarationService.createDeclaration(
                sampleUser, 2026, 3, 30000000.0, 0.0, 0.0, 0.0, 1, 0.0
        );

        assertNotNull(dec);
        assertNotNull(dec.getId());
        assertEquals("u-test-99", dec.getUserId());
        assertEquals(30000000.0, dec.getTotalGrossIncome());
        assertTrue(dec.getTaxAmount() > 0);
        assertEquals("PENDING", dec.getStatus());
    }

    @Test
    @DisplayName("TC-DEC-02: Tạo tờ khai thu nhập dưới ngưỡng thuế -> Tự động chuyển trạng thái PAID")
    void testCreateDeclarationZeroTaxAutoPaid() {
        TaxDeclaration dec = declarationService.createDeclaration(
                sampleUser, 2026, 4, 10000000.0, 0.0, 0.0, 0.0, 0, 0.0
        );
        assertEquals(0.0, dec.getTaxAmount());
        assertEquals("PAID", dec.getStatus());
    }

    @Test
    @DisplayName("TC-DEC-03: Tìm kiếm tờ khai theo ID")
    void testFindById() {
        TaxDeclaration dec = declarationService.createDeclaration(
                sampleUser, 2026, 5, 20000000.0, 0.0, 0.0, 0.0, 0, 0.0
        );
        Optional<TaxDeclaration> found = declarationService.findById(dec.getId());
        assertTrue(found.isPresent());
        assertEquals(20000000.0, found.get().getGrossSalary());
    }

    @Test
    @DisplayName("TC-DEC-04: Lọc danh sách tờ khai theo năm, tháng và trạng thái")
    void testFilterDeclarations() {
        List<TaxDeclaration> list = declarationService.getAllDeclarations(2026, null, "ALL", null);
        assertNotNull(list);
        assertTrue(list.size() >= 2);

        List<TaxDeclaration> pendingList = declarationService.getAllDeclarations(null, null, "PENDING", null);
        for (TaxDeclaration d : pendingList) {
            assertEquals("PENDING", d.getStatus());
        }
    }

    @Test
    @DisplayName("TC-DEC-05: Tìm kiếm tờ khai theo từ khóa (MST / Tên)")
    void testSearchByKeyword() {
        List<TaxDeclaration> list = declarationService.getAllDeclarations(null, null, "ALL", "0123456789");
        assertFalse(list.isEmpty());
        for (TaxDeclaration d : list) {
            assertTrue(d.getTaxCode().contains("0123456789"));
        }
    }

    @Test
    @DisplayName("TC-DEC-06: Xuất báo cáo quyết toán thuế định dạng CSV chuẩn UTF-8 BOM")
    void testExportToCsv() {
        String csv = declarationService.exportToCsv(2026, null, "ALL");
        assertNotNull(csv);
        // Kiểm tra UTF-8 BOM \uFEFF
        assertTrue(csv.startsWith("\uFEFF"), "Báo cáo CSV phải bắt đầu bằng UTF-8 BOM để Excel hiển thị tiếng Việt");
        assertTrue(csv.contains("Mã Tờ Khai,Họ và Tên,Mã Số Thuế"));
        assertTrue(csv.contains("Nguyễn Văn A"));
    }
}
