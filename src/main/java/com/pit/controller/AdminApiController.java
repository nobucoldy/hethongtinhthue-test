package com.pit.controller;

import com.pit.model.SystemConfig;
import com.pit.model.TaxDeclaration;
import com.pit.model.User;
import com.pit.service.ConfigService;
import com.pit.service.DeclarationService;
import com.pit.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final DeclarationService declarationService;
    private final ConfigService configService;
    private final UserService userService;

    @Autowired
    public AdminApiController(DeclarationService declarationService,
                              ConfigService configService,
                              UserService userService) {
        this.declarationService = declarationService;
        this.configService = configService;
        this.userService = userService;
    }

    /**
     * Kiểm tra quyền quản trị viên
     */
    private boolean checkAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    /**
     * Nhóm 2.1: Theo dõi danh sách tờ khai toàn hệ thống kèm bộ lọc
     */
    @GetMapping("/declarations")
    public ResponseEntity<?> getDeclarations(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session) {

        if (!checkAdmin(session)) {
            return ResponseEntity.status(403).body(createError("Yêu cầu quyền Quản trị viên / Cán bộ thuế!"));
        }

        List<TaxDeclaration> list = declarationService.getAllDeclarations(year, month, status, keyword);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("total", list.size());
        resp.put("data", list);
        return ResponseEntity.ok(resp);
    }

    /**
     * Nhóm 2.2: Xuất báo cáo quyết toán thuế dạng CSV (Excel đọc chuẩn tiếng Việt)
     */
    @GetMapping("/reports/export")
    public ResponseEntity<?> exportReport(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "status", required = false) String status,
            HttpSession session) {

        if (!checkAdmin(session)) {
            return ResponseEntity.status(403).body(createError("Yêu cầu quyền Quản trị viên / Cán bộ thuế!"));
        }

        String csvData = declarationService.exportToCsv(year, month, status);
        byte[] bytes = csvData.getBytes(StandardCharsets.UTF_8);

        String filename = "Bao_Cao_Thue_TNCN_" + (year != null ? year : "Toan_Bo") + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    /**
     * Nhóm 2.3: Xem cấu hình hệ thống hiện tại
     */
    @GetMapping("/config")
    public ResponseEntity<?> getConfig(HttpSession session) {
        if (!checkAdmin(session)) {
            return ResponseEntity.status(403).body(createError("Yêu cầu quyền Quản trị viên / Cán bộ thuế!"));
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("config", configService.getConfig());
        return ResponseEntity.ok(resp);
    }

    /**
     * Nhóm 2.3: Cập nhật cấu hình hệ thống (Mức giảm trừ, Tỷ lệ bảo hiểm)
     */
    @PostMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody Map<String, Object> req, HttpSession session) {
        if (!checkAdmin(session)) {
            return ResponseEntity.status(403).body(createError("Yêu cầu quyền Quản trị viên / Cán bộ thuế!"));
        }

        try {
            double personalDeduction = getDouble(req, "personalDeduction", 11000000.0);
            double dependentDeduction = getDouble(req, "dependentDeduction", 4400000.0);
            double bhxhRate = getDouble(req, "bhxhRate", 0.08);
            double bhytRate = getDouble(req, "bhytRate", 0.015);
            double bhtnRate = getDouble(req, "bhtnRate", 0.01);
            double insuranceBaseSalaryCap = getDouble(req, "insuranceBaseSalaryCap", 46800000.0);

            if (personalDeduction < 0 || dependentDeduction < 0 || bhxhRate < 0 || bhytRate < 0 || bhtnRate < 0) {
                return ResponseEntity.badRequest().body(createError("Các thông số cấu hình không được là số âm!"));
            }

            configService.updateConfig(personalDeduction, dependentDeduction, bhxhRate, bhytRate, bhtnRate, insuranceBaseSalaryCap);

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Cập nhật tham số cấu hình hệ thống thành công!");
            resp.put("config", configService.getConfig());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createError("Lỗi cập nhật cấu hình: " + e.getMessage()));
        }
    }

    /**
     * Khôi phục cấu hình hệ thống về mặc định
     */
    @PostMapping("/config/reset")
    public ResponseEntity<?> resetConfig(HttpSession session) {
        if (!checkAdmin(session)) {
            return ResponseEntity.status(403).body(createError("Yêu cầu quyền Quản trị viên / Cán bộ thuế!"));
        }
        configService.resetToDefault();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "Đã khôi phục cấu hình mặc định theo Luật Thuế hiện hành!");
        resp.put("config", configService.getConfig());
        return ResponseEntity.ok(resp);
    }

    /**
     * Danh sách người nộp thuế đã đăng ký
     */
    @GetMapping("/taxpayers")
    public ResponseEntity<?> getTaxpayers(HttpSession session) {
        if (!checkAdmin(session)) {
            return ResponseEntity.status(403).body(createError("Yêu cầu quyền Quản trị viên!"));
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", userService.getAllTaxpayers());
        return ResponseEntity.ok(resp);
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

    private Map<String, Object> createError(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", message);
        return map;
    }
}
