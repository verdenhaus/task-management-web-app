package com.example.taskmanagementwebapp.controller;

import com.example.taskmanagementwebapp.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends BaseIntegrationTest {

    @Test
    void registerThenLogin_Success() throws Exception {
        String unique = UUID.randomUUID().toString().substring(0, 8);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username":"__test__user%s","email":"__test__%s@mail.com","password":"pass1234"}
                            """.formatted(unique, unique)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"__test__%s@mail.com","password":"pass1234"}
                            """.formatted(unique)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_InvalidPassword_Fails() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"__test__fake@mail.com","password":"wrong"}
                            """))
                .andExpect(status().is4xxClientError());
    }
}
