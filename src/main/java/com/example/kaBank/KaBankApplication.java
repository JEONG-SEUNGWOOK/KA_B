package com.example.kaBank;

import com.example.kaBank.services.DTO.HistoryDTO;
import com.example.kaBank.services.Repository.AccountRepository;
import com.example.kaBank.services.DTO.AccountDTO;
import com.example.kaBank.services.Repository.HistoryRepository;
import com.example.kaBank.util.BCrypt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.util.Date;

@SpringBootApplication
public class KaBankApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(KaBankApplication.class, args);
		AccountRepository accountRepository = context.getBean(AccountRepository.class);
		HistoryRepository historyRepository = context.getBean(HistoryRepository.class);

		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setId("admin");
		accountDTO.setPasswrod(BCrypt.hashpw("1234", BCrypt.gensalt()));
		accountDTO.setUserName("관리자");

		accountRepository.save(accountDTO);


		HistoryDTO historyDTO = new HistoryDTO();
		historyDTO.setKeyword("카카오");

		historyRepository.save(historyDTO);historyDTO.setKeyword("카카");

		historyRepository.save(historyDTO);
	}

}
