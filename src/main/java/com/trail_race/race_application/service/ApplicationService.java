package com.trail_race.race_application.service;

import com.trail_race.race_application.model.Application;
import com.trail_race.race_application.dto.request.ApplicationRequest;
import com.trail_race.race_application.dto.response.ApplicationResponse;
import com.trail_race.race_application.dto.EventType;
import com.trail_race.race_application.exception.EventNotFoundException;
import com.trail_race.race_application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository repository;

    public ApplicationResponse getApplication(Integer id) {
        Optional<Application> application = repository.findById(id);
        return application.map(this::getApplicationResponse).orElse(null);
    }

    public List<ApplicationResponse> getAllApplications() {
        return repository.findAll().stream()
                .map(this::getApplicationResponse)
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "application-topic", groupId = "application-group")
    public void processProductEvents(ApplicationRequest applicationRequest) {
        EventType eventType = applicationRequest.getEventType();
        if (eventType.equals(EventType.CREATE_APPLICATION)) {
            repository.save(createApplication(applicationRequest, new Application()));
            log.info("Application is created...");
        } else if (eventType.equals(EventType.UPDATE_APPLICATION)) {
            Optional<Application> existingApplication = repository.findById(applicationRequest.getId());
            existingApplication.ifPresent(
                    application -> repository.save(createApplication(applicationRequest, application)));
            log.info("Application is updated...");
        } else if (eventType.equals(EventType.DELETE_APPLICATION)) {
            Optional<Application> existingApplication = repository.findById(applicationRequest.getId());
            existingApplication.ifPresent(
                    application -> repository.deleteById(applicationRequest.getId()));
            log.info("Application is deleted...");
        } else {
            log.warn("Event is not found or is null.");
            throw new EventNotFoundException(
                    "Event " + applicationRequest.getEventType().name() + " is not recognized or is null");
        }
    }

    private Application createApplication(ApplicationRequest applicationRequest, Application application) {
        application.setFirstName(applicationRequest.getFirstName());
        application.setLastName(applicationRequest.getLastName());
        application.setClub(applicationRequest.getClub());
        application.setDistance(applicationRequest.getDistance());
        return application;
    }

    private ApplicationResponse getApplicationResponse(Application application) {
        return ApplicationResponse.builder()
                .firstName(application.getFirstName())
                .lastName(application.getLastName())
                .club(application.getClub())
                .distance(application.getDistance())
                .id(application.getId())
                .build();
    }
}
