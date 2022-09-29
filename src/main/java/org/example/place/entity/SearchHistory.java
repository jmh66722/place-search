package org.example.place.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "SEARCH_HISTORY", indexes = @Index(name = "idx_keyword", columnList = "keyword"))
@EntityListeners(AuditingEntityListener.class)
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "KEYWORD", nullable = false)
    private String keyword;

    @Lob
    @Column(name = "RESULT")
    private String result;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;
}
