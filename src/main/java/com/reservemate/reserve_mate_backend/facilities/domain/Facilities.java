package com.reservemate.reserve_mate_backend.facilities.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Facilities extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "facilities_id", updatable = false)
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
}
