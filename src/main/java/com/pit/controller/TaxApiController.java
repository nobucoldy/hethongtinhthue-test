package com.pit.controller;

import com.pit.model.PaymentReceipt;
import com.pit.model.TaxCalculationResult;
import com.pit.model.TaxDeclaration;
import com.pit.model.User;
import com.pit.service.ConfigService;
import com.pit.service.DeclarationService;
import com.pit.service.PaymentService;
import com.pit.service.TaxCalculatorService;
import com.pit.util.ValidationUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tax")
public class TaxApiController {

    private final TaxCalculatorService taxCalculatorService;
    private final DeclarationService declarationService;
    private final PaymentService paymentService;
    private final ConfigService configService;

    @Autowired
    public TaxApiController(TaxCalculatorService taxCalculatorService,
                            DeclarationService declarationService,
                            PaymentService paymentService,
                            ConfigService configService) {
        this.taxCalculatorService = taxCalculatorService;
        this.declarationService = declarationService;
        this.paymentService = paymentService;
        this.configService = configService;
    }

    /**
     * API Tính thuế nhanh (dùng cho widget tính nhẩm và form kê khai trực quan)
     */
    @PostMapping("/quick-calculate")
    public ResponseEntity<?> quickCalculate(@RequestBody Map<String, Object> req) {
        try {
            double grossSalary = getDouble(req, "grossSalary", 0.0);
            double bonusIncome = getDouble(req, "bonusIncome", 0.0);
            double otherIncome = getDouble(req, "otherIncome", 0.0);
            double nonTaxableIncome = getDouble(req, "nonTaxableIncome", 0.0);
            int dependentCount = getInt(req, "dependentCount", 0);
            double charityDonation = getDouble(req, "charityDonation", 0.0);

            if (!ValidationUtil.isValidIncome(grossSalary) ||
                !ValidationUtil.isValidIncome(bonusIncome) ||
                !ValidationUtil.isValidIncome(otherIncome) ||
                !ValidationUtil.isValidIncome(nonTaxableIncome) ||
                !ValidationUtil.isValidIncome(charityDonation)) {
                return ResponseEntity.badRequest().body(createError("Thu nhập hoặc các khoản giảm trừ không được âm!"));
            }

            if (!ValidationUtil.isValidDependentCount(dependentCount)) {
                return ResponseEntity.badRequest().body(createError("Số người phụ thuộc phải từ 0 đến 50!"));
            }

            TaxCalculationResult result = taxCalculatorService.calculate(
                    grossSalary, bonusIncome, otherIncome, nonTaxableIncome, dependentCount, charityDonation, configService.getConfig()
            );

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("data", result);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createError("Lỗi tính thuế: " + e.getMessage()));
        }
    }

    /**
     * API Nộp tờ khai thuế
     */
    @PostMapping("/declare")
    public ResponseEntity<?> submitDeclaration(@RequestBody Map<String, Object> req, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(createError("Vui lòng đăng nhập để nộp tờ khai thuế!"));
        }

        try {
            int taxYear = getInt(req, "taxYear", 2026);
            int taxMonth = getInt(req, "taxMonth", 1);
            double grossSalary = getDouble(req, "grossSalary", 0.0);
            double bonusIncome = getDouble(req, "bonusIncome", 0.0);
            double otherIncome = getDouble(req, "otherIncome", 0.0);
            double nonTaxableIncome = getDouble(req, "nonTaxableIncome", 0.0);
            int dependentCount = getInt(req, "dependentCount", 0);
            double charityDonation = getDouble(req, "charityDonation", 0.0);

            if (!ValidationUtil.isValidTaxPeriod(taxYear, taxMonth)) {
                return ResponseEntity.badRequest().body(createError("Kỳ khai thuế không hợp lệ!"));
            }
            if (!ValidationUtil.isValidIncome(grossSalary)) {
                return ResponseEntity.badRequest().body(createError("Lương chính không được âm!"));
            }
            if (!ValidationUtil.isValidDependentCount(dependentCount)) {
                return ResponseEntity.badRequest().body(createError("Số người phụ thuộc phải từ 0 đến 50!"));
            }

            TaxDeclaration dec = declarationService.createDeclaration(
                    user, taxYear, taxMonth, grossSalary, bonusIncome, otherIncome, nonTaxableIncome, dependentCount, charityDonation
            );

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Nộp tờ khai thuế thành công!");
            resp.put("data", dec);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createError("Lỗi nộp tờ khai: " + e.getMessage()));
        }
    }

    /**
     * API Lấy danh sách tờ khai của người dùng đang đăng nhập
     */
    @GetMapping("/my-declarations")
    public ResponseEntity<?> getMyDeclarations(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(createError("Vui lòng đăng nhập!"));
        }

        List<TaxDeclaration> list = declarationService.getDeclarationsByUser(user.getId(), user.getUsername());
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", list);
        return ResponseEntity.ok(resp);
    }

    /**
     * API Đóng thuế trực tuyến
     */
    @PostMapping("/pay")
    public ResponseEntity<?> payTax(@RequestBody Map<String, Object> req, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(createError("Vui lòng đăng nhập để đóng thuế!"));
        }

        String declarationId = (String) req.get("declarationId");
        String paymentMethod = (String) req.get("paymentMethod");

        if (declarationId == null || declarationId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createError("Mã tờ khai không được để trống!"));
        }

        try {
            PaymentReceipt receipt = paymentService.payTax(declarationId, user, paymentMethod);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Nộp thuế thành công! Biên lai điện tử đã được phát hành.");
            resp.put("receipt", receipt);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createError(e.getMessage()));
        }
    }

    /**
     * API Tra cứu biên lai nộp thuế
     */
    @GetMapping("/receipt/{id}")
    public ResponseEntity<?> getReceipt(@PathVariable("id") String id) {
        Optional<PaymentReceipt> opt = paymentService.findById(id);
        if (opt.isPresent()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("receipt", opt.get());
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(404).body(createError("Không tìm thấy biên lai: " + id));
    }

    private double getDouble(Map<String, Object> map, String key, double defaultVal) {
        Object val = map.get(key);
        if (val == null) return defaultVal;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try {
            return Double.parseDouble(val.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private int getInt(Map<String, Object> map, String key, int defaultVal) {
        Object val = map.get(key);
        if (val == null) return defaultVal;
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            return Integer.parseInt(val.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private Map<String, Object> createError(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", message);
        return map;
    }
}
