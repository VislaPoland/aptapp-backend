package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.AccountRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel
@Data
public abstract class SlotDto {
    @ApiModelProperty(value = "Slot ID", required = true)
    private long id;
    @ApiModelProperty(value = "Begin time of the slot", required = true)
    private Date beginTime;
    @ApiModelProperty(value = "Start time of the slot", required = true)
    private Date endTime;
    @ApiModelProperty(value = "Duration of the slot units", required = true)
    private int unitDurationMinutes;
    @ApiModelProperty(value = "Slot units", required = true, dataType = "List[com.creatix.domain.dto.SlotUnitDto]")
    private List<SlotUnitDto> units;
    @ApiModelProperty(value = "Slot target role", required = true, dataType = "List[java.lang.Long]", notes = "Slots will be shown only to accounts with same role.")
    private AccountRole targetRole;
}
