package com.creatix;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.TimeZone;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles(TestContext.PROFILE)
@Transactional
@Component
public abstract class TestContext {

    public static final String PROFILE = "test";

    @PersistenceContext
    protected EntityManager em;

    @Before
    public void before() {
        final Session session = em.unwrap(Session.class);
        session.clear();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Bratislava"));
    }
}
