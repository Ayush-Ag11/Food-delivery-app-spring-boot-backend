package com.project.backend.foodelicious.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_partner", indexes = {
        @Index(name = "idx_delivery_partner_user", columnList = "user_id"),
        @Index(name = "idx_delivery_partner_available", columnList = "isAvailable")
})
public class DeliveryPartner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    private boolean isAvailable;

    @Column(nullable = false)
    private String vehicleId;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point currentLocation;
}
