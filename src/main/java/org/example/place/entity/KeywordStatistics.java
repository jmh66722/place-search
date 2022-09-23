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

    @Column(name = "SEARCH_COUNT", nullable = false)
    @ColumnDefault("0")
    private Integer searchCount;

    @LastModifiedDate
    @Column(name = "MODIFIED_AT", nullable = false)
    private Instant modifiedAt;
}
