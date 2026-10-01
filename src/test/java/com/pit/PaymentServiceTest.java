package com.pit;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YC 6: Unit Test - Đóng thuế & Sinh biên lai (PaymentService)")
public class PaymentServiceTest {

    private DeclarationService declarationService;
    private PaymentService paymentService;
    private User sampleUser;
    private TaxDeclaration pendingDec;

    @BeforeEach
    void setUp() {
        TaxCalculatorService calculatorService = new TaxCalculatorService();
        ConfigService configService = new ConfigService();
        declarationService = new DeclarationService(calculatorService, configService);
        paymentService = new PaymentService(declarationService);

        sampleUser = new User("u-pay-1", "userpay", "Pass@123", "Trần Đóng Thuế",
                "pay@gmail.com", "0911223344", "0123456789", "001200077777", "TAXPAYER");

        pendingDec = declarationService.createDeclaration(
                sampleUser, 2026, 6, 40000000.0, 0.0, 0.0, 0.0, 0, 0.0
        );
    }

    @Test
    @DisplayName("TC-PAY-01: Đóng thuế thành công -> Sinh biên lai và đổi trạng thái PAID")
    void testPayTaxSuccess() {
        assertEquals("PENDING", pendingDec.getStatus());
        assertTrue(pendingDec.getTaxAmount() > 0);

        PaymentReceipt receipt = paymentService.payTax(pendingDec.getId(), sampleUser, "VNPAY_QR");
        assertNotNull(receipt);
        assertNotNull(receipt.getReceiptId());
        assertNotNull(receipt.getTransactionRef());
        assertEquals("VNPAY_QR", receipt.getPaymentMethod());
        assertEquals(pendingDec.getTaxAmount(), receipt.getAmount());
        assertEquals("SUCCESS", receipt.getStatus());

        // Kiểm tra tờ khai đã cập nhật trạng thái
        TaxDeclaration updated = declarationService.findById(pendingDec.getId()).get();
        assertEquals("PAID", updated.getStatus());
        assertEquals(receipt.getReceiptId(), updated.getPaymentReceiptId());
    }

    @Test
    @DisplayName("TC-PAY-02: Đóng thuế cho tờ khai đã nộp -> Ném ngoại lệ IllegalStateException")
    void testPayAlreadyPaidDeclaration() {
        paymentService.payTax(pendingDec.getId(), sampleUser, "BANK_TRANSFER");

        assertThrows(IllegalStateException.class, () -> {
            paymentService.payTax(pendingDec.getId(), sampleUser, "BANK_TRANSFER");
        });
    }

    @Test
    @DisplayName("TC-PAY-03: Đóng thuế cho tờ khai không tồn tại -> Ném ngoại lệ IllegalArgumentException")
    void testPayNonExistentDeclaration() {
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.payTax("DEC-INVALID-99999", sampleUser, "VNPAY_QR");
        });
    }

    @Test
    @DisplayName("TC-PAY-04: Tra cứu biên lai điện tử theo ID và DeclarationID")
    void testFindReceipt() {
        PaymentReceipt receipt = paymentService.payTax(pendingDec.getId(), sampleUser, "CREDIT_CARD");

        Optional<PaymentReceipt> foundById = paymentService.findById(receipt.getReceiptId());
        assertTrue(foundById.isPresent());
        assertEquals(receipt.getReceiptId(), foundById.get().getReceiptId());

        Optional<PaymentReceipt> foundByDec = paymentService.findByDeclarationId(pendingDec.getId());
        assertTrue(foundByDec.isPresent());
        assertEquals(receipt.getReceiptId(), foundByDec.get().getReceiptId());
    }
}
