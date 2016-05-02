package com.creatix;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.TimeZone;

/**
 * Extend this class if you want to run your tests in the spring context.
 * If you want to override default spring boot configuration options
 * make changes in 'resources/application-test.properties' file.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = AptAppBackendApplication.class)
@ActiveProfiles("test")
@Component
@Transactional
public abstract class TestContext {

    @PersistenceContext
    protected EntityManager em;

    @Before
    public void before() {
        final Session session = em.unwrap(Session.class);
        session.clear();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Bratislava"));
    }
}
