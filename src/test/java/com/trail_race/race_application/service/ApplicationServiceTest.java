package com.trail_race.race_application.service;

import com.trail_race.race_application.model.Application;
import com.trail_race.race_application.dto.request.ApplicationRequest;
import com.trail_race.race_application.dto.response.ApplicationResponse;
import com.trail_race.race_application.dto.EventType;
import com.trail_race.race_application.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository repository;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    public void testGetApplicationById() {
        Application mockApplication = new Application();
        mockApplication.setId(1);
        mockApplication.setFirstName("John");
        mockApplication.setLastName("Doe");

        when(repository.findById(1)).thenReturn(Optional.of(mockApplication));

        ApplicationResponse applicationResponse = applicationService.getApplication(1);

        assertNotNull(applicationResponse);
        assertEquals("John", applicationResponse.getFirstName());
        assertEquals("Doe", applicationResponse.getLastName());
    }

    @Test
    public void testGetAllApplications() {
        Application mockApplication1 = new Application();
        mockApplication1.setId(1);
        mockApplication1.setFirstName("John");
        mockApplication1.setLastName("Doe");

        Application mockApplication2 = new Application();
        mockApplication2.setId(2);
        mockApplication2.setFirstName("Jane");
        mockApplication2.setLastName("Smith");

        when(repository.findAll()).thenReturn(Arrays.asList(mockApplication1, mockApplication2));

        List<ApplicationResponse> applicationResponses = applicationService.getAllApplications();

        assertNotNull(applicationResponses);
        assertEquals(2, applicationResponses.size());
        assertEquals("John", applicationResponses.get(0).getFirstName());
        assertEquals("Doe", applicationResponses.get(0).getLastName());
        assertEquals("Jane", applicationResponses.get(1).getFirstName());
        assertEquals("Smith", applicationResponses.get(1).getLastName());
    }

    @Test
    public void testProcessProductEvents_CreateApplication() {
        ApplicationRequest request = new ApplicationRequest();
        request.setEventType(EventType.CREATE_APPLICATION);
        request.setId(1);
        request.setFirstName("John");
        request.setLastName("Doe");

        applicationService.processProductEvents(request);

        verify(repository, times(1)).save(any());
    }

    @Test
    public void testProcessProductEvents_UpdateApplication() {
        ApplicationRequest request = new ApplicationRequest();
        request.setEventType(EventType.UPDATE_APPLICATION);
        request.setId(1);
        request.setFirstName("John");
        request.setLastName("Doe");

        Application existingApplication = new Application();
        existingApplication.setId(1);
        existingApplication.setFirstName("Existing");
        existingApplication.setLastName("Application");

        when(repository.findById(1)).thenReturn(Optional.of(existingApplication));

        applicationService.processProductEvents(request);

        verify(repository, times(1)).save(any());
    }

    @Test
    public void testProcessProductEvents_DeleteApplication() {
        ApplicationRequest request = new ApplicationRequest();
        request.setEventType(EventType.DELETE_APPLICATION);
        request.setId(1);

        Application existingApplication = new Application();
        existingApplication.setId(1);
        existingApplication.setFirstName("John");
        existingApplication.setLastName("Doe");

        when(repository.findById(1)).thenReturn(Optional.of(existingApplication));

        applicationService.processProductEvents(request);

        verify(repository, times(1)).deleteById(1);
    }
}



