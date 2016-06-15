package com.creatix.domain.dto.property;

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

    @ApiModelProperty(value = "Property name", required = true)
    private String name;
    @ApiModelProperty(value = "Property full address", required = true)
    private String address;
    @ApiModelProperty
    private List<Facility> facilities;
    @ApiModelProperty
    private List<Contact> contacts;
    @ApiModelProperty(value = "Property owner info", required = true)
    private Owner owner;


    @ApiModel
    @Data
    public static class Facility {

        @ApiModelProperty(value = "Type of facility", required = true)
        private FacilityType type;
        @ApiModelProperty(value = "Facility info", required = true)
        private List<Info> info;

        @ApiModel
        @Data
        public static class Info {
            @ApiModelProperty(required = true)
            private String name;
            @ApiModelProperty(required = true)
            private String value;

        }
    }

    @ApiModel
    @Data
    public static class Contact {
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
        private String name;
        @ApiModelProperty(required = true)
        private String phone;
        @ApiModelProperty(required = true)
        private String email;
        @ApiModelProperty(required = true)
        private String web;
    }
}