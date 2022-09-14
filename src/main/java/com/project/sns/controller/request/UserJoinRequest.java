package com.project.sns.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class UserJoinRequest {
    
    private String name;
    private String password;
}
