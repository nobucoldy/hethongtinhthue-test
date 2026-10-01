package com.pit.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9}$");
    private static final Pattern CITIZEN_ID_PATTERN = Pattern.compile("^[0-9]{12}$");
    private static final Pattern TAX_CODE_PATTERN = Pattern.compile("^[0-9]{10}(-[0-9]{3})?$");

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.trim().length() >= 6;
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidCitizenId(String citizenId) {
        return citizenId != null && CITIZEN_ID_PATTERN.matcher(citizenId.trim()).matches();
    }

    public static boolean isValidTaxCode(String taxCode) {
        return taxCode != null && TAX_CODE_PATTERN.matcher(taxCode.trim()).matches();
    }

    public static boolean isValidIncome(double income) {
        return !Double.isNaN(income) && !Double.isInfinite(income) && income >= 0.0 && income <= 1_000_000_000_000.0;
    }

    public static boolean isValidDependentCount(int count) {
        return count >= 0 && count <= 50;
    }

    public static boolean isValidTaxPeriod(int year, int month) {
        if (year < 2000 || year > 2100) return false;
        return month >= 0 && month <= 12; // 0: Quyết toán năm, 1-12: Tháng
    }
}
