package com.example.kaBank.services.Repository;

import com.example.kaBank.services.DTO.AccountDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountDTO, String> {

}
