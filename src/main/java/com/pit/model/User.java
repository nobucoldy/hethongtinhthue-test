package com.pit.model;

public class User {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String taxCode;
    private String citizenId;
    private String role; // "TAXPAYER" or "ADMIN"

    public User() {}

    public User(String id, String username, String password, String fullName, 
                String email, String phone, String taxCode, String citizenId, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.taxCode = taxCode;
        this.citizenId = citizenId;
        this.role = role;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
