package com.creatix.domain.dao.notifications;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EntityScan(basePackages = "com.creatix.domain")
@EnableJpaRepositories
public class NotificationReportDaoTestConfiguration {

}
