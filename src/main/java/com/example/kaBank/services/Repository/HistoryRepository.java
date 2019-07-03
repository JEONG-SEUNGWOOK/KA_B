package com.example.kaBank.services.Repository;

import com.example.kaBank.services.DTO.HistoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface HistoryRepository extends JpaRepository<HistoryDTO, String> {
    //히스토리 검색
    public List<HistoryDTO> findAllByOrderBySearchTimeDesc();

    //최근 인기검색어 top 10
    @Query(nativeQuery = true, value = "SELECT CONCAT(s.keyword, '(', s.count, ' 회)') as name, s.keyword AS value, s.keyword AS text FROM History s ORDER BY s.count DESC LIMIT 10")
    public List<Map> findTop10ByOrderByCountDesc();
}
