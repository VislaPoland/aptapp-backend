package com.creatix.domain.dto.notification;

import com.creatix.domain.dto.business.BusinessProfileDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Tomas Michalek on 30/06/2017.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessProfileNotificationDto extends NotificationDto {


    private BusinessProfileDto businessProfile;

}
