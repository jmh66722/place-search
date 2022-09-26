package org.example.place.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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
@Entity(name = "KEYWORD_STATISTICS")
@EntityListeners(AuditingEntityListener.class)
public class KeywordStatistics {

    @Id
    @Column(name = "KEYWORD", nullable = false)
    private String keyword;

    @ColumnDefault("0")
    @Column(name = "TOTAL_COUNT", nullable = false)
    private Integer totalCount;

//    @ColumnDefault("0")
//    @Column(name = "DAY_COUNT", nullable = false)
//    private Integer dayCount;
//
//    @ColumnDefault("0")
//    @Column(name = "HOUR_COUNT", nullable = false)
//    private Integer hourCount;

    @LastModifiedDate
    @Column(name = "MODIFIED_AT", nullable = false)
    private Instant modifiedAt;
}
