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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class AccountDaoTest {

    @Autowired
    private AccountDao accountDao;

    // TODO should be really changed to test data properly without need to use of import.sql
    //  at first create data in @Before method and after do necessary tests
    //  right now this test do nothing but exists
    @Test
    @WithMockCustomUser("apt@test.com")
    public void findByRolesAndPropertyId() throws Exception {
        List<Long> propertyIdList = new ArrayList<>();
        propertyIdList.add(1L);

        final List<Account> accounts = accountDao.findByRolesAndPropertyIdList(AccountRole.values(), propertyIdList, null ,null, null);
        assertNotNull(accounts);
        assertEquals(9, accounts.size());

        final List<Account> accountsAll = accountDao.findByRolesAndPropertyIdList(AccountRole.values(), Collections.emptyList(), null, null, null);
        assertNotNull(accountsAll);
        assertEquals(10, accountsAll.size());
    }

}