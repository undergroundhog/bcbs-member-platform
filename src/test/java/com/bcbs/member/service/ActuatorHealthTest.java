package com.bcbs.member.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.annotation.Import;



@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class ActuatorHealthTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpoint_shouldReturnUp() throws Exception{

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void livenessEndpoint_shouldReturnUp() throws Exception{
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void readinessEndpoint_shouldReturnUp() throws Exception{
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void infoEndpoint_shouldReturnApplicationInfo() throws Exception{
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name")
                .value("BCBS Member Platform"));
    }

    @Test
    void metrics_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "member-admin", roles = "MEMBER_ADMIN")
    void metrics_shouldReturn403_withoutActuatorRole() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "actuator-user", roles = "ACTUATOR")
    void metrics_shouldReturn200_withActuatorRole() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk());
    }

//    @Test
//    @WithMockUser(username = "actuator-user", roles = "ACTUATOR")
//    void unapprovedActuatorEndpoint_shouldBeDenied() throws Exception {
//        mockMvc.perform(get("/actuator/env"))
//                .andExpect(status().isForbidden());
//    }
}

