package com.paymybuddy.finance.modelmemory;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompteMemory {
    private static List<CompteMemory> comptes = new ArrayList<>();

    static {
	comptes.add(new CompteMemory("ami@gmail.com"));
	comptes.add(new CompteMemory("business@yahoo.com"));
	comptes.add(new CompteMemory("mentor@openclassrooms.com"));
    }

    private String email;

    public static List<CompteMemory> getComptes() {
	return comptes;
    }
}
