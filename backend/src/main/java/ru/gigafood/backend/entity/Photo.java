package ru.gigafood.backend.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "photos")
@Builder
public class Photo {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long attachId;

  private String attachTitle;

  @Column(nullable = false, updatable = false)
  private LocalDate uploadDate;

  private String extension;

  private String downloadLink;

  @ManyToOne
  @JsonIgnore
  private User user;

}