package com.pit.service;

import com.pit.model.SystemConfig;
import com.pit.model.TaxCalculationResult;
import com.pit.model.TaxDeclaration;
import com.pit.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DeclarationService {

    private final Map<String, TaxDeclaration> declarationMap = new ConcurrentHashMap<>();
    private final TaxCalculatorService taxCalculatorService;
    private final ConfigService configService;

    @Autowired
    public DeclarationService(TaxCalculatorService taxCalculatorService, ConfigService configService) {
        this.taxCalculatorService = taxCalculatorService;
        this.configService = configService;
        initSampleDeclarations();
    }

    private void initSampleDeclarations() {
        // Tạo vài tờ khai mẫu phục vụ kiểm thử và demo
        TaxDeclaration d1 = new TaxDeclaration();
        d1.setId("DEC-1001");
        d1.setUserId("user-sample-1");
        d1.setFullName("Nguyễn Văn A");
        d1.setTaxCode("0123456789");
        d1.setCitizenId("001200012345");
        d1.setTaxYear(2026);
        d1.setTaxMonth(1);
        d1.setGrossSalary(25000000.0);
        d1.setBonusIncome(5000000.0);
        d1.setOtherIncome(0.0);
        d1.setNonTaxableIncome(0.0);
        d1.setDependentCount(1);
        d1.setCharityDonation(0.0);
        d1.setTotalGrossIncome(30000000.0);
        d1.setTaxableIncome(30000000.0);
        
        TaxCalculationResult calc1 = taxCalculatorService.calculate(25000000.0, 5000000.0, 0, 0, 1, 0, configService.getConfig());
        d1.setTotalDeductions(calc1.getTotalDeductions());
        d1.setAssessedIncome(calc1.getAssessedIncome());
        d1.setTaxAmount(calc1.getTotalTax());
        d1.setNetIncome(calc1.getNetIncome());
        d1.setStatus("PAID");
        d1.setPaymentReceiptId("REC-88001");
        d1.setCreatedAt("2026-02-10 09:30:00");
        declarationMap.put(d1.getId(), d1);

        TaxDeclaration d2 = new TaxDeclaration();
        d2.setId("DEC-1002");
        d2.setUserId("user-sample-1");
        d2.setFullName("Nguyễn Văn A");
        d2.setTaxCode("0123456789");
        d2.setCitizenId("001200012345");
        d2.setTaxYear(2026);
        d2.setTaxMonth(2);
        d2.setGrossSalary(45000000.0);
        d2.setBonusIncome(0.0);
        d2.setOtherIncome(0.0);
        d2.setNonTaxableIncome(0.0);
        d2.setDependentCount(2);
        d2.setCharityDonation(1000000.0);
        
        TaxCalculationResult calc2 = taxCalculatorService.calculate(45000000.0, 0, 0, 0, 2, 1000000.0, configService.getConfig());
        d2.setTotalGrossIncome(calc2.getTotalGrossIncome());
        d2.setTaxableIncome(calc2.getTaxableIncome());
        d2.setTotalDeductions(calc2.getTotalDeductions());
        d2.setAssessedIncome(calc2.getAssessedIncome());
        d2.setTaxAmount(calc2.getTotalTax());
        d2.setNetIncome(calc2.getNetIncome());
        d2.setStatus("PENDING");
        d2.setCreatedAt("2026-03-05 14:15:00");
        declarationMap.put(d2.getId(), d2);
    }

    public synchronized TaxDeclaration createDeclaration(User user, int taxYear, int taxMonth,
                                                        double grossSalary, double bonusIncome, double otherIncome,
                                                        double nonTaxableIncome, int dependentCount, double charityDonation) {
        TaxCalculationResult calc = taxCalculatorService.calculate(
                grossSalary, bonusIncome, otherIncome, nonTaxableIncome, dependentCount, charityDonation, configService.getConfig()
        );

        TaxDeclaration dec = new TaxDeclaration();
        dec.setId("DEC-" + System.currentTimeMillis() % 1000000);
        dec.setUserId(user.getId());
        dec.setFullName(user.getFullName());
        dec.setTaxCode(user.getTaxCode());
        dec.setCitizenId(user.getCitizenId());
        dec.setTaxYear(taxYear);
        dec.setTaxMonth(taxMonth);
        dec.setGrossSalary(grossSalary);
        dec.setBonusIncome(bonusIncome);
        dec.setOtherIncome(otherIncome);
        dec.setNonTaxableIncome(nonTaxableIncome);
        dec.setDependentCount(dependentCount);
        dec.setCharityDonation(charityDonation);
        dec.setTotalGrossIncome(calc.getTotalGrossIncome());
        dec.setTaxableIncome(calc.getTaxableIncome());
        dec.setTotalDeductions(calc.getTotalDeductions());
        dec.setAssessedIncome(calc.getAssessedIncome());
        dec.setTaxAmount(calc.getTotalTax());
        dec.setNetIncome(calc.getNetIncome());
        dec.setStatus(calc.getTotalTax() == 0 ? "PAID" : "PENDING"); // Nếu không phải nộp thuế thì hoàn tất luôn
        dec.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        declarationMap.put(dec.getId(), dec);
        return dec;
    }

    public synchronized Optional<TaxDeclaration> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(declarationMap.get(id));
    }

    public synchronized List<TaxDeclaration> getDeclarationsByUser(String userId, String username) {
        return declarationMap.values().stream()
                .filter(d -> (userId != null && userId.equals(d.getUserId())) ||
                             (username != null && ("nguyenvana".equalsIgnoreCase(username) && "user-sample-1".equals(d.getUserId()))))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public synchronized List<TaxDeclaration> getAllDeclarations(Integer taxYear, Integer taxMonth, String status, String keyword) {
        return declarationMap.values().stream()
                .filter(d -> {
                    if (taxYear != null && taxYear > 0 && d.getTaxYear() != taxYear) return false;
                    if (taxMonth != null && taxMonth > 0 && d.getTaxMonth() != taxMonth) return false;
                    if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status) && !status.equalsIgnoreCase(d.getStatus())) return false;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        String kw = keyword.trim().toLowerCase();
                        boolean matchName = d.getFullName() != null && d.getFullName().toLowerCase().contains(kw);
                        boolean matchTaxCode = d.getTaxCode() != null && d.getTaxCode().contains(kw);
                        boolean matchCitizenId = d.getCitizenId() != null && d.getCitizenId().contains(kw);
                        boolean matchId = d.getId() != null && d.getId().toLowerCase().contains(kw);
                        return matchName || matchTaxCode || matchCitizenId || matchId;
                    }
                    return true;
                })
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public synchronized void updateStatus(String declarationId, String status, String receiptId) {
        TaxDeclaration dec = declarationMap.get(declarationId);
        if (dec != null) {
            dec.setStatus(status);
            if (receiptId != null) {
                dec.setPaymentReceiptId(receiptId);
            }
        }
    }

    /**
     * Xuất dữ liệu tờ khai thuế dạng CSV báo cáo quyết toán (hỗ trợ UTF-8)
     */
    public synchronized String exportToCsv(Integer year, Integer month, String status) {
        List<TaxDeclaration> list = getAllDeclarations(year, month, status, null);
        StringBuilder sb = new StringBuilder();
        // BOM cho Excel mở tiếng Việt không lỗi font
        sb.append("\uFEFF");
        sb.append("Mã Tờ Khai,Họ và Tên,Mã Số Thuế,CCCD,Năm,Tháng,Tổng Thu Nhập,Giảm Trừ,Thu Nhập Tính Thuế,Thuế TNCN,Thực Nhận,Trạng Thái,Ngày Khai\n");
        for (TaxDeclaration d : list) {
            sb.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,%.0f,%.0f,%.0f,%.0f,%.0f,\"%s\",\"%s\"\n",
                    d.getId(),
                    d.getFullName() != null ? d.getFullName() : "",
                    d.getTaxCode() != null ? d.getTaxCode() : "",
                    d.getCitizenId() != null ? d.getCitizenId() : "",
                    d.getTaxYear(),
                    d.getTaxMonth(),
                    d.getTotalGrossIncome(),
                    d.getTotalDeductions(),
                    d.getAssessedIncome(),
                    d.getTaxAmount(),
                    d.getNetIncome(),
                    d.getStatus(),
                    d.getCreatedAt()
            ));
        }
        return sb.toString();
    }
}
