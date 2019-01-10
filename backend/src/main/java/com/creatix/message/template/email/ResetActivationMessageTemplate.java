package com.creatix.message.template.email;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.EmailTemplateName;
import com.google.common.collect.ImmutableSet;

public class ResetActivationMessageTemplate extends ActivationMessageTemplate {

    private static final ImmutableSet WEB_RESET_ROLES = ImmutableSet.of(AccountRole.Administrator, AccountRole.PropertyOwner);

    public ResetActivationMessageTemplate(Account account, ApplicationProperties properties) {
        super(account, properties);
    }

    @Override
    public String getSubject() {
        return "Activation code for Apt. App - A smarter way to communicate";
    }

    @Override
    public String getTemplateName() {
        return (WEB_RESET_ROLES.contains(account.getRole())) ? EmailTemplateName.ACTIVATION_RESET_WEB.getValue() : EmailTemplateName.ACTIVATION_RESET.getValue();
    }
}
