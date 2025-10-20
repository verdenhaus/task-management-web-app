package com.example.taskmanagementwebapp.controller;

import com.example.taskmanagementwebapp.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends BaseIntegrationTest {

    private String jwt;

    @BeforeEach
    void setup() throws Exception {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String email = "__test__userctrl" + unique + "@mail.com";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username":"__test__userctrl%s","email":"%s","password":"p12345"}
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
    void listUsers_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void createDuplicateEmail_ReturnsBadRequest() throws Exception {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String duplicateEmail = "__test__dup_" + unique + "@mail.com";

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username":"__test__dup1_%s","email":"%s","password":"p123"}
                            """.formatted(unique, duplicateEmail)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username":"__test__dup2_%s","email":"%s","password":"p123"}
                            """.formatted(unique, duplicateEmail)))
                .andExpect(status().isBadRequest());
    }
}
