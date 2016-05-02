package com.creatix;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This component will listen to context reload
 */
@Component
public class ContextListener {

    @EventListener({ContextRefreshedEvent.class})
    void contextRefreshedEvent() {
        // initialize Geometry JSON (de)serializer
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JtsModule());
    }

}
