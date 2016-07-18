package com.creatix.domain.dao;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.entity.store.account.Account;
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

import java.util.ArrayList;
import java.util.Collections;
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
        List<Long> propertyIdList = new ArrayList<>();
        propertyIdList.add(1L);

        final List<Account> accounts = accountDao.findByRolesAndPropertyIdList(AccountRole.values(), propertyIdList);
        assertNotNull(accounts);
        assertEquals(7, accounts.size());

        final List<Account> accountsAll = accountDao.findByRolesAndPropertyIdList(AccountRole.values(), Collections.emptyList());
        assertNotNull(accountsAll);
        assertEquals(8, accountsAll.size());
    }

}