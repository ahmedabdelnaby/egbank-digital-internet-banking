package com.egbank.digitalinternetbanking.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@DiscriminatorValue("EMPLOYEE")
@Data
public class Employee extends User {

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "position")
    private String position;
}