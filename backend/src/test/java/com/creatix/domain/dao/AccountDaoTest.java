package com.creatix.domain.dao;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.entity.account.Account;
import com.creatix.domain.enums.AccountRole;
import com.creatix.mock.WithMockCustomUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class AccountDaoTest {

    @Autowired
    private AccountDao accountDao;

    @Test
    @WithMockCustomUser("apt@test.com")
    public void findByRolesAndPropertyId() throws Exception {
        final List<Account> accounts = accountDao.findByRolesAndPropertyId(AccountRole.values(), 1L);
        assertNotNull(accounts);
        assertEquals(5, accounts.size());

        final List<Account> accountsAll = accountDao.findByRolesAndPropertyId(AccountRole.values(), null);
        assertNotNull(accountsAll);
        assertEquals(6, accountsAll.size());
    }

}