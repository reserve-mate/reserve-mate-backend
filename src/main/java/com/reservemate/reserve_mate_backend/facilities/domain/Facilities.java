package com.reservemate.reserve_mate_backend.facilities.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
public class Facilities {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(length = 100)
  private String name;

  @Column(length = 300)
  private String description;

  @Column(length = 100)
  private String address;

  @Column(length = 100)
  private String latitude;

  @Column(length = 100)
  private String longitude;

  @Column(length = 11)
  private String contactPhone;

  @CreatedDate
  @Column(updatable = false)
  private String createdAt;

  @LastModifiedDate private String updatedAt;
}
