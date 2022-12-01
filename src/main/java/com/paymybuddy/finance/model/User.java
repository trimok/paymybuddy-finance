package com.paymybuddy.finance.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String email;
    private String password;
    private List<Transaction> transactions = new ArrayList<>();
    private List<Compte> comptesTo = new ArrayList<>();
    private List<Compte> comptesFrom = new ArrayList<>();
}
