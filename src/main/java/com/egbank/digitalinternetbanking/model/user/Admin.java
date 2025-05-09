package com.egbank.digitalinternetbanking.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@DiscriminatorValue("ADMIN")
@Data
public class Admin extends Employee {

    @Column(name = "security_clearance")
    private String securityClearance;

    @Column(name = "admin_privileges")
    private String adminPrivileges;
}