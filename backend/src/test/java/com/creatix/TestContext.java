package com.creatix;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.TimeZone;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles("test")
@Transactional
@Component
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