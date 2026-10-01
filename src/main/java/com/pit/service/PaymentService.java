package com.pit.service;

import com.pit.model.PaymentReceipt;
import com.pit.model.TaxDeclaration;
import com.pit.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {

    private final Map<String, PaymentReceipt> receiptMap = new ConcurrentHashMap<>();
    private final DeclarationService declarationService;

    @Autowired
    public PaymentService(DeclarationService declarationService) {
        this.declarationService = declarationService;

        // Biên lai mẫu cho demo
        PaymentReceipt r1 = new PaymentReceipt("REC-88001", "DEC-1001", "user-sample-1", 
                "Nguyễn Văn A", "0123456789", 250000.0, "VNPAY_QR", "TXN-" + System.currentTimeMillis());
        receiptMap.put(r1.getReceiptId(), r1);
    }

    public synchronized PaymentReceipt payTax(String declarationId, User user, String paymentMethod) {
        Optional<TaxDeclaration> opt = declarationService.findById(declarationId);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy tờ khai mã: " + declarationId);
        }

        TaxDeclaration dec = opt.get();
        if ("PAID".equalsIgnoreCase(dec.getStatus())) {
            throw new IllegalStateException("Tờ khai này đã được nộp thuế hoàn tất!");
        }

        String receiptId = "REC-" + (10000 + (System.currentTimeMillis() % 90000));
        String txnRef = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PaymentReceipt receipt = new PaymentReceipt(
                receiptId,
                dec.getId(),
                user.getId(),
                user.getFullName(),
                user.getTaxCode(),
                dec.getTaxAmount(),
                paymentMethod != null ? paymentMethod : "VNPAY_QR",
                txnRef
        );

        receiptMap.put(receiptId, receipt);
        declarationService.updateStatus(dec.getId(), "PAID", receiptId);

        return receipt;
    }

    public synchronized Optional<PaymentReceipt> findById(String receiptId) {
        if (receiptId == null) return Optional.empty();
        return Optional.ofNullable(receiptMap.get(receiptId));
    }

    public synchronized Optional<PaymentReceipt> findByDeclarationId(String declarationId) {
        if (declarationId == null) return Optional.empty();
        return receiptMap.values().stream()
                .filter(r -> declarationId.equals(r.getDeclarationId()))
                .findFirst();
    }
}
