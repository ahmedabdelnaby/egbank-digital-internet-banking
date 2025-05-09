package com.egbank.digitalinternetbanking.repository;

import com.egbank.digitalinternetbanking.model.config.SystemConfig;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface SystemConfigRepository extends JpaRepository<SystemConfig, String> {
}