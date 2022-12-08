package com.paymybuddy.finance.session;

import java.util.List;

import com.paymybuddy.finance.modelmemory.CompteMemory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Context {
    private String transfertCompteTo;
    private String transfertCompteFrom;
    private String newCompteTo;
    private int transfertAmount;
    private String description;

    private List<CompteMemory> comptes = CompteMemory.getComptes();
}
