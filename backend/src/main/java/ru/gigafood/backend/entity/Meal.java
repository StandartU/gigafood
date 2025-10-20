package ru.gigafood.backend.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "calories_etimated")
    @Min(0)
    private Integer caloriesEstimated;

    @Column(name = "fats_etimated")
    @Min(0)
    private Integer fatsEstimated;

    @Column(name = "carbs_estimatedd")
    @Min(0)
    private Integer carbsEstimated;
    
    @Column(name = "manual_correction")
    @Min(0)
    private String manualCorrection;

    @Column(name = "calories_corrected")
    @Min(0)
    private Integer caloriesCorrected;

    @Column(name = "meal_time")
    private Date mealTime;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
