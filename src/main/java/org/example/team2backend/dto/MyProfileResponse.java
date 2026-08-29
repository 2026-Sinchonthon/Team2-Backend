package org.example.team2backend.dto;

import lombok.Getter;
import org.example.team2backend.entity.School;
import org.example.team2backend.entity.User;

@Getter
public class MyProfileResponse {

    private final String name;

    private final School school;

    public MyProfileResponse(User user) {
        this.name = user.getName();
        this.school = user.getSchool();
    }
}
