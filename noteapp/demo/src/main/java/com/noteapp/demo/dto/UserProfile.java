package com.noteapp.demo.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfile {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}


