package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.AccountRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel("Data needed to create new slot")
@Data
public class PersistSlotRequest {
    @ApiModelProperty(value = "Time when slot starts", required = true)
    private Date beginTime;
    @ApiModelProperty(value = "Slot duration in minutes", required = true)
    private int durationMinutes;
    @ApiModelProperty(value = "Initial slot unit capacity (1 = 1 person capacity)", required = true)
    private int initialCapacity;
    @ApiModelProperty(value = "Slot target role", required = true, dataType = "List[java.lang.Long]", notes = "Slots will be shown only to accounts with same role.")
    private AccountRole targetRole;
}
