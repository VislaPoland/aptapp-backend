package com.creatix.domain.dto.property;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.enums.CommunicationType;
import com.creatix.domain.enums.ContactType;
import com.creatix.domain.enums.FacilityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    private AddressDto fullAddress;
    @ApiModelProperty
    private List<Facility> facilities;
    @ApiModelProperty
    private List<Contact> contacts;
    @ApiModelProperty(value = "Property owner info", required = true)
    private Owner owner;
    @ApiModelProperty(value = "Property schedule", required = true)
    private Schedule schedule;

    @ApiModel
    @Data
    public static class Facility {
        @ApiModelProperty(value = "Facility ID", required = true)
        private Long id;
        @ApiModelProperty(value = "Type of facility", required = true)
        private FacilityType type;
        @ApiModelProperty(value = "Facility info", required = true)
        private List<Detail> details;

        @ApiModel
        @Data
        public static class Detail {
            @ApiModelProperty(required = true)
            private String name;
            @ApiModelProperty(required = true)
            private String value;
            @ApiModelProperty(required = true)
            private Integer ordinal;
        }
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
    @Data
    public static class Owner {
        @ApiModelProperty(required = true)
        private String id;
        @ApiModelProperty(required = true)
        private String name;
        @ApiModelProperty(required = true)
        private String phone;
        @ApiModelProperty(required = true)
        private String email;
        @ApiModelProperty(required = true)
        private String web;
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
        @ApiModelProperty(value = "Number of slots per schedule time", required = true)
        private Integer slotsCount;
    }
}