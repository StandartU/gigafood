package ru.gigafood.backend.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;


@Entity
@Table(name = "weekly_report")
public class WeeklyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "weekly_start_date")
    private Date weeklyStartDate;

    @Column(name = "weekly_end_date")
    private Date weeklyEndDate;

    @Column(name = "total_calories")
    @Min(0)
    private Integer totalCalories;

    @Column(columnDefinition = "TEXT", name = "recomendations")
    private String recomendations;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
