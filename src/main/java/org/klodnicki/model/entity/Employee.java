package org.klodnicki.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.klodnicki.model.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee extends Person {

    public Employee (String firstName, String lastName, String email, Department department, Salary salary, String birthPlace,
                     LocalDate birthDate, Gender gender, Address address, String telephoneNumber, String bankAccountNumber,
                     String peselOrNip, LocalDate dateOfEmployment) {
        super(firstName, lastName, email);

        if(email == null && telephoneNumber == null)
            throw new IllegalArgumentException("Email or telephone number must be provided!");

        this.department = department;
        this.salary = salary;
        this.birthPlace = birthPlace;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.telephoneNumber = telephoneNumber;
        this.bankAccountNumber = bankAccountNumber;
        this.peselOrNip = peselOrNip;
        this.dateOfEmployment = dateOfEmployment;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Enumerated(EnumType.STRING) //instead of 0,1,2 I want to keep Strings for clarity
    private Department department;
    @Embedded
    private Salary salary;
    private String birthPlace;
    private LocalDate birthDate;
    @Enumerated(EnumType.STRING) //instead of 0,1,2 I want to keep Strings for clarity
    private Gender gender;
    @Embedded
    private Address address;
    private String telephoneNumber;
    private String bankAccountNumber;
    private String peselOrNip;
    private LocalDate dateOfEmployment;

}
