package com.paymybuddy.finance.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Compte {
    private static List<Compte> comptes = new ArrayList<>();

    static {
	comptes.add(new Compte("ami@gmail.com"));
	comptes.add(new Compte("business@yahoo.com"));
	comptes.add(new Compte("mentor@openclassrooms.com"));
    }

    private String email;

    public static List<Compte> getComptes() {
	return comptes;
    }
}
