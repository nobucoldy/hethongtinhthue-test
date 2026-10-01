package com.pit;

import com.pit.controller.AuthController;
import com.pit.model.User;
import com.pit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YC 6: Unit Test - Xác thực người dùng (AuthController)")
public class AuthControllerTest {

    private AuthController authController;
    private UserService userService;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        authController = new AuthController(userService);
        session = new MockHttpSession();
    }

    @Test
    @DisplayName("TC-AUTH-01: Đăng nhập thành công với tài khoản hợp lệ")
    void testLoginSuccess() {
        Map<String, String> body = new HashMap<>();
        body.put("username", "nguyenvana");
        body.put("password", "User@123");

        ResponseEntity<?> response = authController.login(body, session);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(session.getAttribute("user"));
    }

    @Test
    @DisplayName("TC-AUTH-02: Đăng nhập thất bại khi sai mật khẩu")
    void testLoginWrongPassword() {
        Map<String, String> body = new HashMap<>();
        body.put("username", "nguyenvana");
        body.put("password", "WrongPass");

        ResponseEntity<?> response = authController.login(body, session);
        assertEquals(401, response.getStatusCodeValue());
        assertNull(session.getAttribute("user"));
    }

    @Test
    @DisplayName("TC-AUTH-03: Đăng ký thành công tài khoản mới")
    void testRegisterSuccess() {
        Map<String, String> body = new HashMap<>();
        body.put("username", "user_test_reg");
        body.put("password", "Secret123");
        body.put("fullName", "Hoàng Văn Test");
        body.put("email", "hoangtest@gmail.com");
        body.put("phone", "0988776655");
        body.put("taxCode", "8888777766");
        body.put("citizenId", "001200055555");

        ResponseEntity<?> response = authController.register(body, session);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(session.getAttribute("user"));
    }

    @Test
    @DisplayName("TC-AUTH-04: Đăng ký thất bại khi thiếu hoặc sai định dạng dữ liệu")
    void testRegisterInvalidInputs() {
        Map<String, String> body = new HashMap<>();
        body.put("username", "ab"); // quá ngắn
        body.put("password", "123");
        body.put("fullName", "");
        body.put("email", "invalid_email");
        body.put("phone", "123");
        body.put("taxCode", "12");
        body.put("citizenId", "12");

        ResponseEntity<?> response = authController.register(body, session);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("TC-AUTH-05: Lấy thông tin user hiện tại và đăng xuất")
    void testGetCurrentUserAndLogout() {
        // Chưa đăng nhập
        ResponseEntity<?> respBefore = authController.getCurrentUser(session);
        assertEquals(401, respBefore.getStatusCodeValue());

        // Đăng nhập
        User user = new User("u-id", "testuser", "pass", "Test User", "test@gmail.com", "0900000000", "0123456789", "001200000000", "TAXPAYER");
        session.setAttribute("user", user);

        ResponseEntity<?> respAfter = authController.getCurrentUser(session);
        assertEquals(200, respAfter.getStatusCodeValue());

        // Đăng xuất
        ResponseEntity<?> logoutResp = authController.logout(session);
        assertEquals(200, logoutResp.getStatusCodeValue());
        assertTrue(session.isInvalid());
    }
}
