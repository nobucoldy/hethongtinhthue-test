package com.pit.controller;

import com.pit.model.User;
import com.pit.service.UserService;
import com.pit.util.ValidationUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(createResponse(false, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!"));
        }

        Optional<User> userOpt = userService.login(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("user", user);

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Đăng nhập thành công!");
            resp.put("user", sanitizeUser(user));
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(401).body(createResponse(false, "Tên đăng nhập hoặc mật khẩu không chính xác!"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");
        String fullName = body.get("fullName");
        String email = body.get("email");
        String phone = body.get("phone");
        String taxCode = body.get("taxCode");
        String citizenId = body.get("citizenId");

        // Validation
        if (!ValidationUtil.isValidUsername(username)) {
            return ResponseEntity.badRequest().body(createResponse(false, "Tên đăng nhập không hợp lệ (3-30 ký tự chữ, số hoặc gạch dưới)!"));
        }
        if (!ValidationUtil.isValidPassword(password)) {
            return ResponseEntity.badRequest().body(createResponse(false, "Mật khẩu phải có độ dài từ 6 ký tự trở lên!"));
        }
        if (fullName == null || fullName.trim().length() < 2) {
            return ResponseEntity.badRequest().body(createResponse(false, "Vui lòng nhập họ và tên hợp lệ!"));
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(createResponse(false, "Địa chỉ email không đúng định dạng!"));
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            return ResponseEntity.badRequest().body(createResponse(false, "Số điện thoại phải gồm 10 chữ số bắt đầu bằng số 0!"));
        }
        if (!ValidationUtil.isValidTaxCode(taxCode)) {
            return ResponseEntity.badRequest().body(createResponse(false, "Mã số thuế phải gồm 10 chữ số hợp lệ!"));
        }
        if (!ValidationUtil.isValidCitizenId(citizenId)) {
            return ResponseEntity.badRequest().body(createResponse(false, "Căn cước công dân (CCCD) phải gồm 12 chữ số hợp lệ!"));
        }

        try {
            User user = userService.register(username, password, fullName, email, phone, taxCode, citizenId);
            session.setAttribute("user", user);

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Đăng ký tài khoản thành công!");
            resp.put("user", sanitizeUser(user));
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("user", sanitizeUser(user));
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(401).body(createResponse(false, "Chưa đăng nhập"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(createResponse(true, "Đăng xuất thành công!"));
    }

    private Map<String, Object> createResponse(boolean success, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", message);
        return map;
    }

    private Map<String, Object> sanitizeUser(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("fullName", user.getFullName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("taxCode", user.getTaxCode());
        map.put("citizenId", user.getCitizenId());
        map.put("role", user.getRole());
        return map;
    }
}
