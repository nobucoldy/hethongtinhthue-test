package com.pit.service;

import com.pit.model.SystemConfig;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {
    private SystemConfig currentConfig;

    public ConfigService() {
        this.currentConfig = new SystemConfig();
    }

    public synchronized SystemConfig getConfig() {
        return this.currentConfig;
    }

    public synchronized void updateConfig(double personalDeduction, double dependentDeduction,
                                         double bhxhRate, double bhytRate, double bhtnRate,
                                         double insuranceBaseSalaryCap) {
        if (personalDeduction > 0) this.currentConfig.setPersonalDeduction(personalDeduction);
        if (dependentDeduction >= 0) this.currentConfig.setDependentDeduction(dependentDeduction);
        if (bhxhRate >= 0) this.currentConfig.setBhxhRate(bhxhRate);
        if (bhytRate >= 0) this.currentConfig.setBhytRate(bhytRate);
        if (bhtnRate >= 0) this.currentConfig.setBhtnRate(bhtnRate);
        if (insuranceBaseSalaryCap > 0) this.currentConfig.setInsuranceBaseSalaryCap(insuranceBaseSalaryCap);
    }

    public synchronized void resetToDefault() {
        this.currentConfig = new SystemConfig();
    }
}
