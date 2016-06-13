package com.creatix;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AptAppBackendApplication.class)
@ActiveProfiles("test")
@Transactional
@Component
public class ApplicationTests extends TestContext {

	@Test
	public void contextLoads() {
	}

}
