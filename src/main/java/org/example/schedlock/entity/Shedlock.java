package org.example.schedlock.entity;

import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.Instant;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity(name = "SHEDLOCK")
@EntityListeners(AuditingEntityListener.class)
public class Shedlock {

    @Id
    @Column(name = "NAME")
    private String name;

    @Column(name = "LOCK_UNTIL", nullable = false)
    private String lockUntil;

    @LastModifiedDate
    @Column(name = "LOCKED_AT", nullable = false)
    private Instant lockedAt;

    @LastModifiedDate
    @Column(name = "LOCKED_BY", nullable = false)
    private Instant lockedBy;
}
