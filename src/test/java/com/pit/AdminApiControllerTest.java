package com.pit;

import com.pit.controller.AdminApiController;
import com.pit.model.User;
import com.pit.service.ConfigService;
import com.pit.service.DeclarationService;
import com.pit.service.TaxCalculatorService;
import com.pit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YC 6: Unit Test - API Quản trị & Báo cáo (AdminApiController)")
public class AdminApiControllerTest {

    private AdminApiController adminApiController;
    private DeclarationService declarationService;
    private ConfigService configService;
    private UserService userService;
    private MockHttpSession session;
    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        TaxCalculatorService calculatorService = new TaxCalculatorService();
        configService = new ConfigService();
        declarationService = new DeclarationService(calculatorService, configService);
        userService = new UserService();
        adminApiController = new AdminApiController(declarationService, configService, userService);

        session = new MockHttpSession();
        adminUser = new User("admin-id", "admin", "pass", "Cán Bộ Quản Trị", "admin@gdt.gov.vn", "0900000001", "0000000001", "001090000001", "ADMIN");
        regularUser = new User("user-id", "user", "pass", "Người Dân", "user@gmail.com", "0900000002", "0000000002", "001090000002", "TAXPAYER");
    }

    @Test
    @DisplayName("TC-ADM-01: Quyền truy cập bị từ chối nếu không phải Admin")
    void testAccessDeniedForNonAdmin() {
        // Chưa đăng nhập
        ResponseEntity<?> respNoAuth = adminApiController.getDeclarations(null, null, null, null, session);
        assertEquals(403, respNoAuth.getStatusCodeValue());

        // Đăng nhập tài khoản thường
        session.setAttribute("user", regularUser);
        ResponseEntity<?> respRegular = adminApiController.getDeclarations(null, null, null, null, session);
        assertEquals(403, respRegular.getStatusCodeValue());
    }

    @Test
    @DisplayName("TC-ADM-02: Admin xem danh sách tờ khai thành công")
    void testGetDeclarationsAdminSuccess() {
        session.setAttribute("user", adminUser);
        ResponseEntity<?> resp = adminApiController.getDeclarations(2026, null, "ALL", null, session);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    @DisplayName("TC-ADM-03: Admin xuất báo cáo quyết toán thuế dạng CSV")
    void testExportReportCsvSuccess() {
        session.setAttribute("user", adminUser);
        ResponseEntity<?> resp = adminApiController.exportReport(2026, null, "ALL", session);
        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getHeaders().getContentDisposition().isAttachment());
        assertNotNull(resp.getBody());
    }

    @Test
    @DisplayName("TC-ADM-04: Admin xem và cập nhật cấu hình hệ thống")
    void testGetAndUpdateConfig() {
        session.setAttribute("user", adminUser);

        // Xem config
        ResponseEntity<?> getResp = adminApiController.getConfig(session);
        assertEquals(200, getResp.getStatusCodeValue());

        // Cập nhật mức giảm trừ mới (15tr bản thân, 6tr phụ thuộc)
        Map<String, Object> updateReq = new HashMap<>();
        updateReq.put("personalDeduction", 15000000.0);
        updateReq.put("dependentDeduction", 6000000.0);
        updateReq.put("bhxhRate", 0.08);
        updateReq.put("bhytRate", 0.015);
        updateReq.put("bhtnRate", 0.01);
        updateReq.put("insuranceBaseSalaryCap", 46800000.0);

        ResponseEntity<?> updateResp = adminApiController.updateConfig(updateReq, session);
        assertEquals(200, updateResp.getStatusCodeValue());
        assertEquals(15000000.0, configService.getConfig().getPersonalDeduction());

        // Reset về mặc định
        ResponseEntity<?> resetResp = adminApiController.resetConfig(session);
        assertEquals(200, resetResp.getStatusCodeValue());
        assertEquals(15500000.0, configService.getConfig().getPersonalDeduction());
    }

    @Test
    @DisplayName("TC-ADM-05: Cập nhật cấu hình thất bại nếu nhập số âm")
    void testUpdateConfigInvalidNegative() {
        session.setAttribute("user", adminUser);
        Map<String, Object> req = new HashMap<>();
        req.put("personalDeduction", -5000000.0);

        ResponseEntity<?> resp = adminApiController.updateConfig(req, session);
        assertEquals(400, resp.getStatusCodeValue());
    }

    @Test
    @DisplayName("TC-ADM-06: Admin xem danh sách người nộp thuế")
    void testGetTaxpayers() {
        session.setAttribute("user", adminUser);
        ResponseEntity<?> resp = adminApiController.getTaxpayers(session);
        assertEquals(200, resp.getStatusCodeValue());
    }
}
