package org.example.place.repository;

import org.example.place.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory,Integer> {

    Optional<SearchHistory> findTopByKeywordOrderByIdDesc(String keyword);
}
