package com.example.kaBank.services.DTO;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="Account")
public class AccountDTO {
    @Id
    private String id;

    @Column
    private String userName;

    @Column
    private String passwrod;


}
