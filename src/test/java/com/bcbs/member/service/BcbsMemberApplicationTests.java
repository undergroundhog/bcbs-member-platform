package com.bcbs.member.service;



import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import org.springframework.boot.test.context.SpringBootTest;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class BcbsMemberApplicationTests {

    @Test
    void contextLoads(){

    }
}
