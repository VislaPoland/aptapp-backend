package com.creatix.domain.dto.property.slot;

import com.creatix.domain.enums.EventInviteResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel
@Getter
@Setter
public class EventSlotDetailDto extends EventSlotDto {

    @ApiModelProperty(value = "List of responses to the event", notes = "API version 'v1' or newer")
	private List<Rsvp> responses;

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
        @ApiModelProperty(required = true)
        private String unitNumber;
    }

    @ApiModel
    @Getter
    @Setter
    public static class Rsvp {
        @ApiModelProperty(required = true)
        private EventInviteResponse response;
        @ApiModelProperty(required = true)
        private AccountDto attendant;
    }
}
