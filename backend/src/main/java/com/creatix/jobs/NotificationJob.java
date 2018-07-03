package com.creatix.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class NotificationJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.err.println("Hello World!  MyJob is executing.");

    }
}
