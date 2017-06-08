package com.creatix.domain.dto.community.board;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@ApiModel
@Data
@EqualsAndHashCode(of = "id")
public class CommunityBoardCategoryDto {


    @ApiModelProperty("Id")
    private Long id;

    @ApiModelProperty("Title of category")
    @Size(max = 255)
    @NotEmpty
    private String title;

}
