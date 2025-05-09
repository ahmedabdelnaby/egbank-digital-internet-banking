package com.egbank.digitalinternetbanking.model.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "system_configs")
@Data
public class SystemConfig {

    @Id
    private String configKey;
    private String configValue;
}