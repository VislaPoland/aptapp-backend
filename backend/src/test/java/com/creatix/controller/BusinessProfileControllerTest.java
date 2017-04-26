package com.creatix.controller;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.controller.v1.business.BusinessProfileController;
import com.creatix.domain.dao.business.BusinessCategoryDao;
import com.creatix.domain.dao.business.BusinessProfileDao;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.business.BusinessContactDto;
import com.creatix.domain.dto.business.BusinessProfileDto;
import com.creatix.domain.entity.store.business.BusinessCategory;
import com.creatix.domain.mapper.BusinessMapper;
import com.creatix.mock.WithMockCustomUser;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Created by kvimbi on 25/04/2017.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class BusinessProfileControllerTest extends TestContext {

    @Autowired
    BusinessProfileController businessProfileController;
    @Autowired
    BusinessCategoryDao businessCategoryDao;
    @Autowired
    BusinessMapper businessMapper;
    @Autowired
    BusinessProfileDao businessProfileDao;

    @Test
    @WithMockCustomUser("helen.owner@apartments.com")
    public void testCreatingOfBusinessProfile() {
        BusinessCategory businessCategory = businessCategoryDao.findById(1L);
        BusinessContactDto businessContactDto = new BusinessContactDto()
                .setStreet("Sopkova")
                .setHouseNumber("5/A")
                .setState("Mordor")
                .setZipCode("018 41")
                .setCountry("Slovakia");


        BusinessProfileDto request = new BusinessProfileDto()
                .setName("Random name")
                .setDescription("Random description")
                .setContact(businessContactDto)
                .setBusinessCategoryList(Collections.singletonList(
                        businessMapper.toBusinessCategory(businessCategory)
                ));

        DataResponse<BusinessProfileDto> createdBusinessProfile = businessProfileController.createBusinessProfile(request, 1L);
        BusinessProfileDto data = createdBusinessProfile.getData();
        assertNotNull(data.getId());
        assertNotNull(data.getContact());
        assertNotNull(data.getContact().getId());
        assertNotNull(data.getBusinessCategoryList());
        assertEquals(1, data.getBusinessCategoryList().size());
    }



}
