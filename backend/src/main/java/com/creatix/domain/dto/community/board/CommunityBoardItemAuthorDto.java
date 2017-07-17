package com.creatix.domain.dto.community.board;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by Tomas Michalek on 11/05/2017.
 */
@Data
@ApiModel
public class CommunityBoardItemAuthorDto {

    private String firstName;
    private String lastName;
    private String companyName;
    private String primaryPhone;
    private String primaryEmail;

}
