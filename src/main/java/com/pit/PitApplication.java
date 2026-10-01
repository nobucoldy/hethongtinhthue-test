package com.pit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PitApplication {
    public static void main(String[] args) {
        SpringApplication.run(PitApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("   HE THONG TINH THUE THU NHAP CA NHAN (PIT SYSTEM) DA KHOI CHAY  ");
        System.out.println("   Truy cap Web: http://localhost:8080                           ");
        System.out.println("   Tai khoan Admin: admin / Admin@123                            ");
        System.out.println("   Tai khoan User : nguyenvana / User@123                        ");
        System.out.println("==================================================================");
    }
}
