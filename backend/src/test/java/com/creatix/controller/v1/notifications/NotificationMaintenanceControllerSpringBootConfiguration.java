package com.creatix.controller.v1.notifications;

import org.springframework.boot.SpringBootConfiguration;

/**
 * configuration to bypass {@link com.creatix.AptAppBackendApplication} to be able to have Unit test
 * instead of integration tests with many data we don't need
 * <p>
 * TODO should be moved to upper level (must validate it will not break down any of existing tests)
 */
@SpringBootConfiguration
public class NotificationMaintenanceControllerSpringBootConfiguration {

}
