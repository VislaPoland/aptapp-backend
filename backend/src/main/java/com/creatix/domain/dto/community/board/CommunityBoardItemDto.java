package com.creatix.domain.dto.community.board;

import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.enums.community.board.CommunityBoardItemType;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@Data
@ApiModel
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "title"})
public class CommunityBoardItemDto {

    @ApiModelProperty
    private Long id;

    @ApiModelProperty
    @NotEmpty
    @Size(max = 255)
    private String title;

    @ApiModelProperty
    @NotEmpty
    @Size(max = 2048)
    private String description;

    @ApiModelProperty
    @Min(0L)
    private Double price;

    @ApiModelProperty
    @NotNull
    private CommunityBoardCategory category;

    @ApiModelProperty
    @NotNull
    private CommunityBoardItemType communityBoardItemType;

    @ApiModelProperty
    @NotNull
    private CommunityBoardStatusType communityBoardStatus;

    @ApiModelProperty
    private PrivacySettingsDto privacySettings;

    @ApiModelProperty
    private CommunityBoardItemAuthorDto account;

    @ApiModelProperty
    private List<CommunityBoardItemPhotoDto> photoList;

    @ApiModelProperty
    @Past
    private OffsetDateTime createdAt;

    @ApiModelProperty
    @Past
    private OffsetDateTime updatedAt;

}
