package com.trail_race.race_application.dto.response;

import com.trail_race.race_application.model.Distance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApplicationResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String club;
    private Distance distance;
}
