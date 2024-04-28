package com.example.community.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Component
public class InitDataService {

    Logger logger = LoggerFactory.getLogger(JpaInitData.class);

    @Autowired
    @Qualifier("jdbcInitData")
    InitData initData;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        Instant beforeTime = Instant.now();

        initData.init();

        Instant afterTime = Instant.now();
        long diffTime = Duration.between(beforeTime, afterTime).toMillis();
        logger.info("diffTime = "+diffTime);
    }
}
