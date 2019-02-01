package com.creatix.controller;

import com.creatix.AptAppBackendApplication;
import com.creatix.TestContext;
import com.creatix.controller.v1.message.CreatePersonalMessageRequest;
import com.creatix.controller.v1.message.PersonalMessageController;
import com.creatix.controller.v1.message.PersonalMessageRequestType;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.notification.message.PersonalMessageDto;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public class PersonalMessageControllerTest {

    @Autowired
    private PersonalMessageController personalMessageController;

    @Test
    @WithMockCustomUser("mark.building@apartments.com")
    public void createPersonalMessage() {

        final CreatePersonalMessageRequest createMsgReq = new CreatePersonalMessageRequest();
        createMsgReq.setContent("Hello, this is my personal message");
        createMsgReq.setRecipients(Arrays.asList(3L, 451L));
        createMsgReq.setPersonalMessageRequestType(PersonalMessageRequestType.TO_TENANT);
        final DataResponse<List<PersonalMessageDto>> createMsgRes = personalMessageController.createNewPersonalMessage(createMsgReq);
        assertNotNull(createMsgRes);
        assertNotNull(createMsgRes.getData());
        assertEquals(2, createMsgRes.getData().size());
    }

}