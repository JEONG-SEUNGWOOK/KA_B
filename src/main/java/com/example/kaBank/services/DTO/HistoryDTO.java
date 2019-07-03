package com.example.kaBank.services.DTO;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="History")
public class HistoryDTO {
    @Id
    private String keyword;

    @Column
    private LocalDateTime searchTime = LocalDateTime.now();

    @Column
    private Integer count = 1;
}
