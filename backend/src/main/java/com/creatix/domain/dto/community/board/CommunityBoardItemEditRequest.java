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
import javax.validation.constraints.Size;

/**
 * Created by kvimbi on 02/06/2017.
 */
@Data
@ApiModel
@ToString(of = {"id", "title"})
@EqualsAndHashCode(of = "id")
public class CommunityBoardItemEditRequest {

    @ApiModelProperty
    private Long id;

    @NotEmpty
    @Size(max = 255)
    @ApiModelProperty
    private String title;

    @NotEmpty
    @Size(max = 2048)
    @ApiModelProperty
    private String description;

    @Min(0)
    @ApiModelProperty
    private Double price;

    @NotNull
    @ApiModelProperty
    private CommunityBoardCategory category;

    @NotNull
    @ApiModelProperty
    private CommunityBoardItemType communityBoardItemType;

    @NotNull
    @ApiModelProperty
    private CommunityBoardStatusType communityBoardStatus;

    private PrivacySettingsDto privacySettings;

}
