package com.example;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {

    private final InitMemeberService initMemeberService;
    @PostConstruct
    public void init(){
        initMemeberService.init();
    }

    @Component
    static class InitMemeberService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init(){

        }
    }
}
