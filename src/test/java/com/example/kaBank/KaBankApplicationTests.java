package com.example.kaBank;

import com.example.kaBank.services.Repository.AccountRepository;
import com.example.kaBank.services.DTO.AccountDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KaBankApplicationTests {

	@Autowired
	private AccountRepository accountRepository;

	@Test
	public void contextLoads() {
		//given
		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setId("admin");
		accountDTO.setPasswrod("1234");
		accountDTO.setUserName("관리자");

		accountRepository.save(accountDTO);

		//when
		List<AccountDTO> list = accountRepository.findAll();

		//then
		AccountDTO testAccountDTO = list.get(0);
		System.out.println(testAccountDTO.getId());


	}

}
