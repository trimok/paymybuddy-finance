package com.paymybuddy.finance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.paymybuddy.finance.model.Compte;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.session.Context;

@Controller
@SessionAttributes(value = { "context" })
public class FinanceController {

    @PostMapping("/transfert")
    public String transfert(@ModelAttribute("context") Context context) {
	Person person = context.getPerson();
	person.getTransactions().add(new Transaction(context.getTransfertCompteFrom(), context.getTransfertCompteTo(),
		context.getDescription(), context.getTransfertAmount()));
	return "transfert";
    }

    @PostMapping("/addCompteTo")
    public String addConnection(@ModelAttribute("context") Context context) {
	Person person = context.getPerson();
	person.getComptesTo().add(new Compte(context.getNewCompteTo()));
	return "transfert";
    }
}
