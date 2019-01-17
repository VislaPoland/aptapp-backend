package com.creatix.domain.dto.notification.reporting;

import com.creatix.domain.dto.apartment.BasicApartmentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class NotificationReportAccountDto {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String fullName;

    @Setter
    @ApiModelProperty(value = "apartment for tenant. Not set when any apartment related.")
    private BasicApartmentDto apartment;
}
