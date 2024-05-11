package com.trail_race.race_application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trail_race.race_application.dto.EventType;
import com.trail_race.race_application.model.Distance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRequest {

    private EventType eventType;
    private Integer id;
    private String firstName;
    private String lastName;
    private String club;
    private Distance distance;
}

