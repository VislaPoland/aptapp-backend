package com.creatix.domain.dto.community.board;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Created by kvimbi on 02/06/2017.
 */
@Data
@ApiModel("Entity for adding and editing new comments")
public class CommunityBoardCommentEditRequest {

    @ApiModelProperty("Id of comment")
    private Long id;

    @NotBlank
    @Size(max = 2048)
    @ApiModelProperty("Content of comment")
    private String content;

    @ApiModelProperty("Id of parent comment if reply")
    private Long parentCommentId;

}
