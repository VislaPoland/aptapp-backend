package com.creatix.controller;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.controller.v1.business.BusinessProfileController;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.business.BusinessCategoryDao;
import com.creatix.domain.dao.business.BusinessProfileDao;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.business.BusinessCategoryDto;
import com.creatix.domain.dto.business.BusinessContactDto;
import com.creatix.domain.dto.business.BusinessProfileCreateRequest;
import com.creatix.domain.dto.business.BusinessProfileDto;
import com.creatix.domain.entity.store.business.BusinessCategory;
import com.creatix.domain.entity.store.business.BusinessProfile;
import com.creatix.domain.enums.CommunicationType;
import com.creatix.domain.mapper.BusinessMapper;
import com.creatix.mock.WithMockCustomUser;
import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tomas Michalek on 25/04/2017.
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

    private DataResponse<BusinessProfileDto> createBusinessProfile() {
        BusinessCategoryDto businessCategoryDto = new BusinessCategoryDto();
        businessCategoryDto.setId(1L);
        businessCategoryDto.setName("Random name");

        BusinessContactDto businessContactDto = new BusinessContactDto()
                .setCommunicationType(CommunicationType.Phone)
                .setCommunicationValue("+421 902 265 340")
                .setStreet("Sopkova")
                .setHouseNumber("5/A")
                .setState("Mordor")
                .setZipCode("018 41");


        BusinessProfileCreateRequest request = new BusinessProfileCreateRequest()
                .setName("Random name")
                .setDescription("Random description")
                .setContact(businessContactDto)
                .setBusinessCategoryList(Collections.singletonList(businessCategoryDto));

        return businessProfileController.createBusinessProfile(request, 1L);
    }


    @Test
    @Commit
    @WithMockCustomUser("helen.owner@apartments.com")
    public void testCreatingOfBusinessProfile() {
        DataResponse<BusinessProfileDto> createdBusinessProfile = createBusinessProfile();

        BusinessProfileDto data = createdBusinessProfile.getData();
        assertNotNull(data.getId());
        assertNotNull(data.getContact());
        assertNotNull(data.getContact().getId());
        assertNotNull(data.getBusinessCategoryList());
        assertEquals(1, data.getBusinessCategoryList().size());
    }

    @Test
    @Commit
    @WithMockCustomUser("helen.owner@apartments.com")
    public void testProfileUpdate() {
        DataResponse<BusinessProfileDto> businessProfile = createBusinessProfile();
        BusinessProfileDto data = businessProfile.getData();
        data.setBusinessCategoryList(new LinkedList<>());
        DataResponse<BusinessProfileDto> update = businessProfileController.updateBusinessProfile(data, data.getId());
        assertNotNull(update.getData().getBusinessCategoryList());
        assertEquals(0, update.getData().getBusinessCategoryList().size());
    }


}
