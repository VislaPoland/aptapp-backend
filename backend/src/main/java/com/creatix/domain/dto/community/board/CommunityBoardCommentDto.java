package com.creatix.domain.dto.community.board;

import com.creatix.domain.enums.community.board.CommunityBoardCommentStatusType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created by Tomas Michalek on 12/05/2017.
 */
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "parentCommentId"})
@Data
@ApiModel
public class CommunityBoardCommentDto {

    @ApiModelProperty
    private Long id;

    @ApiModelProperty(value = "Comment content", required = true)
    @Size(max = 2048)
    @NotEmpty
    private String content;

    @ApiModelProperty("Comment author")
    private CommunityBoardItemAuthorDto author;

    @ApiModelProperty("Parent comment id if nested comment")
    private Long parentCommentId;

    @Past
    @ApiModelProperty("Time and date of creation")
    private OffsetDateTime createdAt;

    @ApiModelProperty("Comment status")
    private CommunityBoardCommentStatusType status;

    @ApiModelProperty
    private List<CommunityBoardCommentDto> childComments;

}
