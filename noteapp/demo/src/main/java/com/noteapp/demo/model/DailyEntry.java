package com.noteapp.demo.model;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_entries")
public class DailyEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private LocalDate date;
    private double hours;


}
