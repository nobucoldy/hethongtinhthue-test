package com.pit;

import com.pit.model.User;
import com.pit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YC 6: Unit Test - Quản lý người dùng (UserService)")
public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    @DisplayName("TC-USR-01: Đăng ký người nộp thuế thành công")
    void testRegisterSuccess() {
        User user = userService.register("newuser", "Password@123", "Lê Văn C",
                "levanc@gmail.com", "0933123456", "0312345678", "001200088888");
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("newuser", user.getUsername());
        assertEquals("TAXPAYER", user.getRole());

        Optional<User> found = userService.findByUsername("newuser");
        assertTrue(found.isPresent());
        assertEquals("Lê Văn C", found.get().getFullName());
    }

    @Test
    @DisplayName("TC-USR-02: Đăng ký trùng tên đăng nhập -> Ném ngoại lệ")
    void testRegisterDuplicateUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register("nguyenvana", "Password@123", "Trùng Tên",
                    "dup@gmail.com", "0933123456", "0312345678", "001200088888");
        });
    }

    @Test
    @DisplayName("TC-USR-03: Đăng nhập thành công với tài khoản đúng")
    void testLoginSuccess() {
        Optional<User> user = userService.login("nguyenvana", "User@123");
        assertTrue(user.isPresent());
        assertEquals("Nguyễn Văn A", user.get().getFullName());

        Optional<User> admin = userService.login("admin", "Admin@123");
        assertTrue(admin.isPresent());
        assertEquals("ADMIN", admin.get().getRole());
    }

    @Test
    @DisplayName("TC-USR-04: Đăng nhập thất bại khi sai mật khẩu hoặc sai username")
    void testLoginFailure() {
        Optional<User> wrongPass = userService.login("nguyenvana", "SaiMatKhau");
        assertFalse(wrongPass.isPresent());

        Optional<User> wrongUser = userService.login("khongtontai", "User@123");
        assertFalse(wrongUser.isPresent());
    }

    @Test
    @DisplayName("TC-USR-05: Lấy danh sách toàn bộ người nộp thuế (không bao gồm Admin)")
    void testGetAllTaxpayers() {
        List<User> list = userService.getAllTaxpayers();
        assertNotNull(list);
        assertTrue(list.size() >= 2);
        for (User u : list) {
            assertEquals("TAXPAYER", u.getRole());
        }
    }
}
