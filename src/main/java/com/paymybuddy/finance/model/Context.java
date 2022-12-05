package com.paymybuddy.finance.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Context {
    private Person user;
    private String transfertCompteTo;
    private String transfertCompteFrom;
    private String newCompteTo;
    private int transfertAmount;
    private String description;

    private List<Compte> comptes = Compte.getComptes();
}
