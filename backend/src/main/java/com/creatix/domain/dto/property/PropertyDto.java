package com.creatix.domain.dto.property;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.dto.property.slot.MaintenanceSlotScheduleDto;
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
public class PropertyDto {

    @ApiModelProperty(value = "Property ID", required = true)
    private Long id;
    @ApiModelProperty(value = "Property name", required = true)
    private String name;
    @ApiModelProperty(value = "Property address", required = true)
    private AddressDto address;
    @ApiModelProperty(value = "Full address", required = true)
    private String fullAddress;
    @ApiModelProperty(value = "Time zone", required = true)
    private String timeZone;
    @ApiModelProperty
    private List<FacilityDto> facilities;
    @ApiModelProperty
    private List<ContactDto> contacts;
    @ApiModelProperty(value = "Property owner info", required = true)
    private OwnerDto owner;
    @ApiModelProperty(value = "Property managers")
    private List<AccountDto> managers;
    @ApiModelProperty(value = "Property assistants of managers")
    private List<AccountDto> assistantManagers;
    @ApiModelProperty(value = "Property employees")
    private List<AccountDto> employees;
    @ApiModelProperty(value = "Property schedule", required = true)
    private MaintenanceSlotScheduleDto schedule;
    @ApiModelProperty(value = "Notification photo")
    private List<PropertyPhotoDto> photos;
    @ApiModelProperty(value = "Enable/disable sms notifications", required = true, notes = "Indicate that sms message notifications are enabled/disabled")
    private Boolean enableSms;

    @ApiModel
    @Data
    public static class FacilityDto {
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
    public static class ContactDto {
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
    public static class OwnerDto extends AccountDto {
        @ApiModelProperty(required = true)
        private String web;
    }

    @ApiModel
    @Getter
    @Setter
    public static class AccountDto {
        @ApiModelProperty(required = true)
        private String id;
        @ApiModelProperty(required = true)
        private AccountRole role;
        @ApiModelProperty(required = true)
        private String firstName;
        @ApiModelProperty(required = true)
        private String lastName;
        @ApiModelProperty(required = true)
        private String fullName;
        @ApiModelProperty(required = true)
        private String phone;
        @ApiModelProperty(required = true)
        private String email;
        @ApiModelProperty
        private String companyName;
        @ApiModelProperty
        private Boolean deleted;
    }

}