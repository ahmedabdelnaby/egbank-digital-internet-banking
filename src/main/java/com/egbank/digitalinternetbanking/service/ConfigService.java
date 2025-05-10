package com.egbank.digitalinternetbanking.service;

import com.egbank.digitalinternetbanking.model.config.SystemConfig;
import com.egbank.digitalinternetbanking.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConfigService {
    private final SystemConfigRepository systemConfigRepository;

    @Autowired
    public ConfigService(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }

    public double getSavingsAccMinBalance() {
        return Double.parseDouble(
                systemConfigRepository.findById("SAVINGS_MINIMUM_BALANCE")
                        .map(SystemConfig::getConfigValue)
                        .orElse("100.0")
        );
    }

    public int getSavingsAccWithdrawalLimit() {
        return Integer.parseInt(
                systemConfigRepository.findById("SAVINGS_WITHDRAWAL_LIMIT")
                        .map(SystemConfig::getConfigValue)
                        .orElse("3")
        );
    }

    public String updateParameter(String key, String value) {
        if (getAllConfigs().stream().noneMatch(
                config -> config.getConfigKey().equals(key))) {
            return "Parameter key not found.";
        }

        if (!value.matches("\\d+(\\.\\d+)?")) {
            return "Invalid value format.";
        }

        SystemConfig config = new SystemConfig();
        config.setConfigKey(key);
        config.setConfigValue(value);
        systemConfigRepository.save(config);
        return "System parameter updated.";
    }

    public List<SystemConfig> getAllConfigs() {
        return systemConfigRepository.findAll();
    }
}