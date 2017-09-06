package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.EventInviteResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@ApiModel
@Getter
@Setter
public class EventSlotDetailDto extends EventSlotDto {

    @ApiModelProperty(value = "List of responses to the event", example = "{'Going':[{'id':0,'firstName':'','lastName':''}], 'Maybe':[]}", dataType = "java.util.Map<EventInviteResponse, java.util.List<AccountDto>>")
	private Map<EventInviteResponse, List<AccountDto>> responses;

	@ApiModel
    @Getter
    @Setter
	public static class AccountDto {
        @ApiModelProperty(required = true)
        private Long id;
        @ApiModelProperty(required = true)
        private String firstName;
        @ApiModelProperty(required = true)
        private String lastName;
    }
}
