package com.trail_race.race_application.controller;

import com.trail_race.race_application.dto.response.ApplicationResponse;
import com.trail_race.race_application.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService applicationService;

    @Test
    public void testGetApplicationById() throws Exception {
        ApplicationResponse mockResponse = ApplicationResponse.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .build();

        when(applicationService.getApplication(1)).thenReturn(mockResponse);

        // Perform and Verify
        mockMvc.perform(MockMvcRequestBuilders.get("/application/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void testGetAllApplications() throws Exception {
        // Setup
        ApplicationResponse mockResponse1 = ApplicationResponse.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .build();

        ApplicationResponse mockResponse2 = ApplicationResponse.builder()
                .id(2)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        List<ApplicationResponse> mockResponses = Arrays.asList(mockResponse1, mockResponse2);

        when(applicationService.getAllApplications()).thenReturn(mockResponses);

        mockMvc.perform(MockMvcRequestBuilders.get("/applications"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Jane"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].lastName").value("Smith"));
    }
}

