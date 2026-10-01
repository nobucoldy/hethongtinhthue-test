package com.pit;

import com.pit.model.*;
import com.pit.model.TaxCalculationResult.BracketDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YC 6: Unit Test - Kiểm thử toàn diện Model Getters/Setters")
public class ModelTest {

    @Test
    void testUserGettersSetters() {
        User u = new User();
        u.setId("1");
        u.setUsername("usr");
        u.setPassword("pwd");
        u.setFullName("Full Name");
        u.setEmail("e@mail.com");
        u.setPhone("0900000000");
        u.setTaxCode("0123456789");
        u.setCitizenId("001200000000");
        u.setRole("ADMIN");

        assertEquals("1", u.getId());
        assertEquals("usr", u.getUsername());
        assertEquals("pwd", u.getPassword());
        assertEquals("Full Name", u.getFullName());
        assertEquals("e@mail.com", u.getEmail());
        assertEquals("0900000000", u.getPhone());
        assertEquals("0123456789", u.getTaxCode());
        assertEquals("001200000000", u.getCitizenId());
        assertEquals("ADMIN", u.getRole());
    }

    @Test
    void testTaxBracketGettersSetters() {
        TaxBracket b = new TaxBracket();
        b.setBracketOrder(1);
        b.setMinIncome(0);
        b.setMaxIncome(5000000);
        b.setTaxRate(0.05);
        b.setQuickSubtract(0);
        b.setDescription("Bậc 1");

        assertEquals(1, b.getBracketOrder());
        assertEquals(0, b.getMinIncome());
        assertEquals(5000000, b.getMaxIncome());
        assertEquals(0.05, b.getTaxRate());
        assertEquals(0, b.getQuickSubtract());
        assertEquals("Bậc 1", b.getDescription());
    }

    @Test
    void testSystemConfigGettersSetters() {
        SystemConfig sc = new SystemConfig();
        sc.setPersonalDeduction(12000000);
        sc.setDependentDeduction(5000000);
        sc.setBhxhRate(0.08);
        sc.setBhytRate(0.015);
        sc.setBhtnRate(0.01);
        sc.setInsuranceBaseSalaryCap(50000000);
        sc.setBrackets(new ArrayList<>());

        assertEquals(12000000, sc.getPersonalDeduction());
        assertEquals(5000000, sc.getDependentDeduction());
        assertEquals(0.08, sc.getBhxhRate());
        assertEquals(0.015, sc.getBhytRate());
        assertEquals(0.01, sc.getBhtnRate());
        assertEquals(50000000, sc.getInsuranceBaseSalaryCap());
        assertNotNull(sc.getBrackets());
    }

    @Test
    void testPaymentReceiptGettersSetters() {
        PaymentReceipt r = new PaymentReceipt();
        r.setReceiptId("R1");
        r.setDeclarationId("D1");
        r.setUserId("U1");
        r.setFullName("Name");
        r.setTaxCode("MST");
        r.setAmount(100000);
        r.setPaymentMethod("QR");
        r.setTransactionRef("REF1");
        r.setPaymentTime("2026-01-01 10:00:00");
        r.setStatus("SUCCESS");

        assertEquals("R1", r.getReceiptId());
        assertEquals("D1", r.getDeclarationId());
        assertEquals("U1", r.getUserId());
        assertEquals("Name", r.getFullName());
        assertEquals("MST", r.getTaxCode());
        assertEquals(100000, r.getAmount());
        assertEquals("QR", r.getPaymentMethod());
        assertEquals("REF1", r.getTransactionRef());
        assertEquals("2026-01-01 10:00:00", r.getPaymentTime());
        assertEquals("SUCCESS", r.getStatus());
    }

    @Test
    void testTaxDeclarationGettersSetters() {
        TaxDeclaration d = new TaxDeclaration();
        d.setId("DEC1");
        d.setUserId("U1");
        d.setFullName("User");
        d.setTaxCode("MST");
        d.setCitizenId("CCCD");
        d.setTaxYear(2026);
        d.setTaxMonth(5);
        d.setGrossSalary(1000);
        d.setBonusIncome(200);
        d.setOtherIncome(100);
        d.setNonTaxableIncome(50);
        d.setDependentCount(2);
        d.setCharityDonation(10);
        d.setTotalGrossIncome(1300);
        d.setTaxableIncome(1250);
        d.setTotalDeductions(500);
        d.setAssessedIncome(750);
        d.setTaxAmount(37.5);
        d.setNetIncome(1200);
        d.setStatus("PENDING");
        d.setCreatedAt("time");
        d.setPaymentReceiptId("REC1");

        assertEquals("DEC1", d.getId());
        assertEquals("U1", d.getUserId());
        assertEquals("User", d.getFullName());
        assertEquals("MST", d.getTaxCode());
        assertEquals("CCCD", d.getCitizenId());
        assertEquals(2026, d.getTaxYear());
        assertEquals(5, d.getTaxMonth());
        assertEquals(1000, d.getGrossSalary());
        assertEquals(200, d.getBonusIncome());
        assertEquals(100, d.getOtherIncome());
        assertEquals(50, d.getNonTaxableIncome());
        assertEquals(2, d.getDependentCount());
        assertEquals(10, d.getCharityDonation());
        assertEquals(1300, d.getTotalGrossIncome());
        assertEquals(1250, d.getTaxableIncome());
        assertEquals(500, d.getTotalDeductions());
        assertEquals(750, d.getAssessedIncome());
        assertEquals(37.5, d.getTaxAmount());
        assertEquals(1200, d.getNetIncome());
        assertEquals("PENDING", d.getStatus());
        assertEquals("time", d.getCreatedAt());
        assertEquals("REC1", d.getPaymentReceiptId());
    }

    @Test
    void testTaxCalculationResultGettersSetters() {
        TaxCalculationResult res = new TaxCalculationResult();
        res.setGrossSalary(100);
        res.setBonusIncome(20);
        res.setOtherIncome(10);
        res.setNonTaxableIncome(5);
        res.setTotalGrossIncome(130);
        res.setTaxableIncome(125);
        res.setPersonalDeduction(11);
        res.setDependentCount(1);
        res.setDependentDeduction(4.4);
        res.setInsuranceBhxh(8);
        res.setInsuranceBhyt(1.5);
        res.setInsuranceBhtn(1);
        res.setTotalInsurance(10.5);
        res.setCharityDonation(0);
        res.setTotalDeductions(25.9);
        res.setAssessedIncome(99.1);
        res.setTotalTax(10);
        res.setNetIncome(109.5);
        res.setCalculationMethod("method");

        BracketDetail bd = new BracketDetail(1, "Desc", 100, 0.05, 5);
        bd.setBracket(2);
        bd.setDescription("D2");
        bd.setIncomeInBracket(200);
        bd.setRate(0.1);
        bd.setTaxAmount(20);

        assertEquals(2, bd.getBracket());
        assertEquals("D2", bd.getDescription());
        assertEquals(200, bd.getIncomeInBracket());
        assertEquals(0.1, bd.getRate());
        assertEquals(20, bd.getTaxAmount());

        res.getBracketDetails().add(bd);
        assertNotNull(res.getBracketDetails());
        assertEquals(100, res.getGrossSalary());
        assertEquals(20, res.getBonusIncome());
        assertEquals(10, res.getOtherIncome());
        assertEquals(5, res.getNonTaxableIncome());
        assertEquals(130, res.getTotalGrossIncome());
        assertEquals(125, res.getTaxableIncome());
        assertEquals(11, res.getPersonalDeduction());
        assertEquals(1, res.getDependentCount());
        assertEquals(4.4, res.getDependentDeduction());
        assertEquals(8, res.getInsuranceBhxh());
        assertEquals(1.5, res.getInsuranceBhyt());
        assertEquals(1, res.getInsuranceBhtn());
        assertEquals(10.5, res.getTotalInsurance());
        assertEquals(0, res.getCharityDonation());
        assertEquals(25.9, res.getTotalDeductions());
        assertEquals(99.1, res.getAssessedIncome());
        assertEquals(10, res.getTotalTax());
        assertEquals(109.5, res.getNetIncome());
        assertEquals("method", res.getCalculationMethod());
    }
}
