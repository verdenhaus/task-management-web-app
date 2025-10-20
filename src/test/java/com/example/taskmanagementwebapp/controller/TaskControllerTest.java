package com.example.taskmanagementwebapp.controller;

import com.example.taskmanagementwebapp.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest extends BaseIntegrationTest {

    private String jwt;

    @BeforeEach
    void setup() throws Exception {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String email = "__test__jwt" + unique + "@mail.com";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username":"__test__jwt%s","email":"%s","password":"p12345"}
                            """.formatted(unique, email)))
                .andExpect(status().isOk());

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"%s","password":"p12345"}
                            """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();

        var json = loginResult.getResponse().getContentAsString();
        jwt = json.substring(json.indexOf(":\"") + 2, json.length() - 2);
    }

    @Test
    void createAndFetchTasks() throws Exception {
        String unique = UUID.randomUUID().toString().substring(0, 8);

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"title":"__test__task_%s","description":"JUnit test task","priorityLevel":"HIGH"}
                            """.formatted(unique)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void assignTask_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/tasks/assign/999999")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest());
    }
}
