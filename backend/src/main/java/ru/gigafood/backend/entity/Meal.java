package ru.gigafood.backend.entity;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
    @JsonIgnore
    private Long id;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "weight_g")
    private Integer weight;

    @Column(name = "calories_etimated")
    @Min(0)
    private Integer caloriesEstimated;

    @Column(name = "manual_correction")
    @Lob
    private String manualCorrection;

    @Column(name = "fats_etimated")
    @Min(0)
    private Integer fatsEstimated;

    @Column(name = "protein_etimated")
    @Min(0)
    private Integer proteinEstimated;

    @Column(name = "carbs_estimatedd")
    @Min(0)
    private Integer carbsEstimated;

    @Column(name = "meal_time")
    private Date mealTime;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false, length = 8)
    private String uuid;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        }
}

}
