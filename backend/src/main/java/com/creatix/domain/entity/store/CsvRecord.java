package com.creatix.domain.entity.store;

import lombok.Getter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
public class CsvRecord {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "Floor cannot be blank")
    private String floor;

    @NotBlank(message = "Unit cannot be blank")
    private String unitNumber;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "The provided email is not valid")
    private String email;

    @Pattern(regexp="^$|^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "The provided phone number is not valid")
    private String phoneNumber;

    public CsvRecord(String firstName, String lastName, String floor, String unitNumber, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.floor = floor;
        this.unitNumber = unitNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getRowDesc() {
        return firstName + " " + lastName + "(" + email + ")";
    }

}
