package com.pit.service;

import com.pit.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    public UserService() {
        // Khởi tạo tài khoản mẫu
        User admin = new User(UUID.randomUUID().toString(), "admin", "Admin@123", "Nguyễn Quản Trị",
                "admin@gdt.gov.vn", "0901234567", "0000000001", "001090000001", "ADMIN");
        User user1 = new User(UUID.randomUUID().toString(), "nguyenvana", "User@123", "Nguyễn Văn A",
                "nguyenvana@gmail.com", "0912345678", "0123456789", "001200012345", "TAXPAYER");
        User user2 = new User(UUID.randomUUID().toString(), "tranthib", "User@123", "Trần Thị B",
                "tranthib@gmail.com", "0987654321", "0987654321", "001200098765", "TAXPAYER");

        userMap.put(admin.getUsername().toLowerCase(), admin);
        userMap.put(user1.getUsername().toLowerCase(), user1);
        userMap.put(user2.getUsername().toLowerCase(), user2);
    }

    public synchronized Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return Optional.ofNullable(userMap.get(username.trim().toLowerCase()));
    }

    public synchronized Optional<User> findById(String id) {
        if (id == null) return Optional.empty();
        return userMap.values().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    public synchronized User register(String username, String password, String fullName,
                                     String email, String phone, String taxCode, String citizenId) {
        if (findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập '" + username + "' đã tồn tại trên hệ thống!");
        }
        User newUser = new User(UUID.randomUUID().toString(), username, password, fullName,
                email, phone, taxCode, citizenId, "TAXPAYER");
        userMap.put(username.toLowerCase(), newUser);
        return newUser;
    }

    public synchronized Optional<User> login(String username, String password) {
        Optional<User> opt = findByUsername(username);
        if (opt.isPresent() && opt.get().getPassword().equals(password)) {
            return opt;
        }
        return Optional.empty();
    }

    public synchronized List<User> getAllTaxpayers() {
        List<User> list = new ArrayList<>();
        for (User u : userMap.values()) {
            if ("TAXPAYER".equalsIgnoreCase(u.getRole())) {
                list.add(u);
            }
        }
        return list;
    }
}
