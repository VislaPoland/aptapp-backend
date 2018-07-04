package com.creatix.jobs;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.message.MessageDeliveryException;
import com.creatix.service.AccountService;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class NotificationJob{
    private static final Logger log = LoggerFactory.getLogger(NotificationJob.class);
    @Autowired
    private AccountService accountService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void resendCodes() throws MessagingException, TemplateException, MessageDeliveryException, IOException {
        log.info("ResendCodes job started");
        for(Account account:  accountService.getInactiveAccounts()){
            Date created = account.getCreatedAt();
            LocalDate dateCreated = created.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date today = new Date();
            LocalDate dateToday = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long daysBetween = DAYS.between(dateCreated, dateToday);

            if( daysBetween == 7 || daysBetween == 14){
                log.info("Resending activation code for accountId: "+account.getId());
                accountService.resendActivationCode(account.getId());
            }
        }
    }
}
