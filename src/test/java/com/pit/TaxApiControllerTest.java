package com.pit;

import com.pit.controller.TaxApiController;
import com.pit.model.PaymentReceipt;
import com.pit.model.TaxDeclaration;
import com.pit.model.User;
import com.pit.service.ConfigService;
import com.pit.service.DeclarationService;
import com.pit.service.PaymentService;
import com.pit.service.TaxCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YC 6: Unit Test - API Thuế & Thanh toán (TaxApiController)")
public class TaxApiControllerTest {

    private TaxApiController taxApiController;
    private DeclarationService declarationService;
    private PaymentService paymentService;
    private MockHttpSession session;
    private User taxpayerUser;

    @BeforeEach
    void setUp() {
        TaxCalculatorService calculatorService = new TaxCalculatorService();
        ConfigService configService = new ConfigService();
        declarationService = new DeclarationService(calculatorService, configService);
        paymentService = new PaymentService(declarationService);
        taxApiController = new TaxApiController(calculatorService, declarationService, paymentService, configService);

        session = new MockHttpSession();
        taxpayerUser = new User("u-tax-1", "taxpayer1", "Pass@123", "Nguyễn Thuế Viên",
                "taxpayer1@gmail.com", "0912121212", "0123456789", "001200033333", "TAXPAYER");
    }

    @Test
    @DisplayName("TC-API-01: API Quick-Calculate tính thuế thành công")
    void testQuickCalculateSuccess() {
        Map<String, Object> req = new HashMap<>();
        req.put("grossSalary", 30000000.0);
        req.put("bonusIncome", 0.0);
        req.put("dependentCount", 1);

        ResponseEntity<?> response = taxApiController.quickCalculate(req);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("TC-API-02: API Quick-Calculate từ chối khi nhập số âm hoặc số phụ thuộc quá lớn")
    void testQuickCalculateInvalid() {
        Map<String, Object> req = new HashMap<>();
        req.put("grossSalary", -1000.0);
        ResponseEntity<?> respNegative = taxApiController.quickCalculate(req);
        assertEquals(400, respNegative.getStatusCodeValue());

        Map<String, Object> req2 = new HashMap<>();
        req2.put("grossSalary", 10000000.0);
        req2.put("dependentCount", 99);
        ResponseEntity<?> respDep = taxApiController.quickCalculate(req2);
        assertEquals(400, respDep.getStatusCodeValue());
    }

    @Test
    @DisplayName("TC-API-03: API Kê khai thuế yêu cầu đăng nhập")
    void testSubmitDeclarationAuthRequired() {
        Map<String, Object> req = new HashMap<>();
        req.put("grossSalary", 25000000.0);

        ResponseEntity<?> response = taxApiController.submitDeclaration(req, session);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("TC-API-04: API Kê khai thuế thành công khi đã đăng nhập")
    void testSubmitDeclarationSuccess() {
        session.setAttribute("user", taxpayerUser);

        Map<String, Object> req = new HashMap<>();
        req.put("taxYear", 2026);
        req.put("taxMonth", 3);
        req.put("grossSalary", 35000000.0);
        req.put("bonusIncome", 5000000.0);
        req.put("dependentCount", 1);

        ResponseEntity<?> response = taxApiController.submitDeclaration(req, session);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("TC-API-05: API Đóng thuế trực tuyến và xem biên lai")
    void testPayTaxAndGetReceipt() {
        session.setAttribute("user", taxpayerUser);

        // Tạo tờ khai
        TaxDeclaration dec = declarationService.createDeclaration(
                taxpayerUser, 2026, 3, 50000000.0, 0, 0, 0, 0, 0
        );

        // Nộp thuế
        Map<String, Object> payReq = new HashMap<>();
        payReq.put("declarationId", dec.getId());
        payReq.put("paymentMethod", "VNPAY_QR");

        ResponseEntity<?> payResp = taxApiController.payTax(payReq, session);
        assertEquals(200, payResp.getStatusCodeValue());

        // Lấy biên lai
        Map<?, ?> body = (Map<?, ?>) payResp.getBody();
        PaymentReceipt receipt = (PaymentReceipt) body.get("receipt");

        ResponseEntity<?> receiptResp = taxApiController.getReceipt(receipt.getReceiptId());
        assertEquals(200, receiptResp.getStatusCodeValue());
    }
}
