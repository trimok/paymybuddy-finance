package com.paymybuddy.finance.modelmemory;

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
public class PersonMemory {
    private String email;
    private String name;
    private String password;
    private List<TransactionMemory> transactions = new ArrayList<>();
    private List<CompteMemory> comptesTo = new ArrayList<>();
    private List<CompteMemory> comptesFrom = new ArrayList<>();
}
