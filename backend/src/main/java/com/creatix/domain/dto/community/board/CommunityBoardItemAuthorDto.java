package com.creatix.domain.dto.community.board;

import com.creatix.domain.entity.store.Apartment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Tomas Michalek on 11/05/2017.
 */
@Data
@ApiModel("Read only property of comment author")
public class CommunityBoardItemAuthorDto {

    @ApiModelProperty
    private Long userId;
    @ApiModelProperty
    private String firstName;
    @ApiModelProperty
    private String lastName;
    @ApiModelProperty
    private String companyName;
    @ApiModelProperty
    private String primaryPhone;
    @ApiModelProperty
    private String primaryEmail;
    @ApiModelProperty
    private CommunityBoardApartmentInfo apartment;

}
