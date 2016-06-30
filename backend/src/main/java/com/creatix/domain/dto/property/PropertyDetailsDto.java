package com.creatix.domain.dto.property;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.CommunicationType;
import com.creatix.domain.enums.ContactType;
import com.creatix.domain.enums.FacilityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel
@Data
public class PropertyDetailsDto {

    @ApiModelProperty(value = "Property ID", required = true)
    private Long id;
    @ApiModelProperty(value = "Property name", required = true)
    private String name;
    @ApiModelProperty(value = "Property address", required = true)
    private AddressDto address;
    @ApiModelProperty(value = "Full address", required = true)
    private String fullAddress;
    @ApiModelProperty
    private List<Facility> facilities;
    @ApiModelProperty
    private List<Contact> contacts;
    @ApiModelProperty(value = "Property owner info", required = true)
    private Owner owner;
    @ApiModelProperty(value = "Property managers")
    private List<Account> managers;
    @ApiModelProperty(value = "Property assistants of managers")
    private List<Account> assistantManagers;
    @ApiModelProperty(value = "Property employees")
    private List<Account> employees;
    @ApiModelProperty(value = "Property schedule", required = true)
    private Schedule schedule;

    @ApiModel
    @Data
    public static class Facility {
        @ApiModelProperty(value = "Facility ID", required = true)
        private Long id;
        @ApiModelProperty(value = "Type of facility", required = true)
        private FacilityType type;
        @ApiModelProperty(value = "Facility name")
        private String name;
        @ApiModelProperty(value = "Facility description")
        private String description;
        @ApiModelProperty(value = "Facility opening hours")
        private String openingHours;
        @ApiModelProperty(value = "Facility location name")
        private String location;
    }

    @ApiModel
    @Data
    public static class Contact {
        @ApiModelProperty(value = "Contact ID", required = true)
        private Long id;
        @ApiModelProperty(value = "Type of contact", required = true)
        private ContactType type;
        @ApiModelProperty(required = true)
        private String value;
        @ApiModelProperty(value = "Type of communication", required = true)
        private CommunicationType communicationType;
    }

    @ApiModel
    @Getter
    @Setter
    public static class Owner extends Account {
        @ApiModelProperty(required = true)
        private String web;
    }

    @ApiModel
    @Getter
    @Setter
    public static class Account {
        @ApiModelProperty(required = true)
        private String id;
        @ApiModelProperty(required = true)
        private AccountRole role;
        @ApiModelProperty(required = true)
        private String name;
        @ApiModelProperty(required = true)
        private String phone;
        @ApiModelProperty(required = true)
        private String email;
        @ApiModelProperty
        private Boolean deleted;
    }

    @ApiModel
    @Data
    public static class Schedule {
        @ApiModelProperty(value = "Start hour of schedule time", required = true, notes = "24-hour clock")
        private Integer startHour;
        @ApiModelProperty(value = "Start minute of schedule time", required = true)
        private Integer startMinute;
        @ApiModelProperty(value = "End hour of schedule time", required = true, notes = "24-hour clock")
        private Integer endHour;
        @ApiModelProperty(value = "End minute of schedule time", required = true)
        private Integer endMinute;
        @ApiModelProperty(value = "Length of the period in minutes", required = true, notes = "This needs to be a divisor of the working time")
        private Integer periodLength;
        @ApiModelProperty(value = "Number of slots per period", required = true)
        private Integer slotsPerPeriod;
    }
}