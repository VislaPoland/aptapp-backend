package com.creatix.domain;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.dao.ApartmentDao;
import com.creatix.domain.dto.account.AccountDto;
import com.creatix.domain.dto.account.PropertyOwnerDto;
import com.creatix.domain.dto.apartment.ApartmentDto;
import com.creatix.domain.dto.property.PropertyDto;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.PropertyOwner;
import com.creatix.mock.WithMockCustomUser;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class MapperTest {

    @Autowired
    private Mapper mapper;
    @Autowired
    private ApartmentDao apartmentDao;
    @Autowired
    private AccountDao accountDao;

    @Test
    @WithMockCustomUser("apt@test.com")
    public void toApartmentDto() throws Exception {
        final Apartment apartment22 = apartmentDao.findById(22L);
        assertNotNull(apartment22.getNeighbors().getLeft());
        final ApartmentDto dto = mapper.toApartmentDto(apartment22);
        assertNotNull(dto.getNeighbors());
        assertNotNull(dto.getNeighbors().getLeft());
    }

    @Test
    public void toAccountDto() {
        final Account owner = accountDao.findById(1L);
        assertNotNull(owner);
        assertTrue(owner instanceof PropertyOwner);
        final PropertyOwner asOwner = (PropertyOwner) owner;

        final AccountDto accountDto = mapper.toAccountDto(owner);
        assertNotNull(accountDto);
        assertTrue("Account is instance of " + accountDto.getClass(), accountDto instanceof PropertyOwnerDto);
        final PropertyOwnerDto ownerDto = (PropertyOwnerDto) accountDto;
        assertNotNull(ownerDto.getOwnedProperties());
        assertEquals(asOwner.getOwnedProperties().size(), ownerDto.getOwnedProperties().size());
        assertEquals(owner.getPrimaryEmail(), ownerDto.getPrimaryEmail());
    }
}