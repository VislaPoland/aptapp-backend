package com.creatix;

import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthenticatedUserDetails;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestUtils {

    public static void setupFakeAdminAccount() {
        Account a = new Account();
        a.setPrimaryEmail("test@admin.com");
        a.setPasswordHash("098765432");
        a.setId(1L);
        a.setRole(AccountRole.Administrator);
        AuthenticatedUserDetails userDetails = new AuthenticatedUserDetails(a);
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(userDetails, userDetails.getAuthorities()));
    }

    public static void login(long accountId, AccountDao accountRepository) {
        final Account account = accountRepository.findById(accountId);
        login(account);
    }

    public static void login(Account account) {
        AuthenticatedUserDetails userDetails = new AuthenticatedUserDetails(account);
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(userDetails, userDetails.getAuthorities()));
    }

}
