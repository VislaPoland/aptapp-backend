package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.AccountRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@ApiModel
@Data
public abstract class SlotDto {
    @ApiModelProperty(value = "Slot ID", required = true)
    private long id;
    @ApiModelProperty(value = "Begin time of the slot", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime beginTime;
    @ApiModelProperty(value = "Start time of the slot", required = true, dataType = "java.lang.String", example = "2016-07-06T11:02:38.564Z")
    private OffsetDateTime endTime;
    @ApiModelProperty(value = "Duration of the slot units", required = true)
    private int unitDurationMinutes;
    @ApiModelProperty(value = "Slot units", required = true, dataType = "List[com.creatix.domain.dto.SlotUnitDto]")
    private List<SlotUnitDto> units;
    @ApiModelProperty(value = "Slot target role", required = true, dataType = "List[java.lang.Long]", notes = "Slots will be shown only to accounts with same role.")
    private AccountRole targetRole;
}
