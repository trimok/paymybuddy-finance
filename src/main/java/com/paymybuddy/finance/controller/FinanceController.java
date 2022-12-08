package com.paymybuddy.finance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.paymybuddy.finance.modelmemory.CompteMemory;
import com.paymybuddy.finance.modelmemory.PersonMemory;
import com.paymybuddy.finance.modelmemory.TransactionMemory;
import com.paymybuddy.finance.session.Context;

@Controller
@SessionAttributes(value = { "context", "person" })
public class FinanceController {

    @PostMapping("/gotoHome")
    public String home() {
	return "home";
    }

    @PostMapping("/gotoTransfer")
    public String transfer() {
	return "transfer";
    }

    @PostMapping("/gotoContact")
    public String contact() {
	return "contact";
    }

    @PostMapping("/gotoProfile")
    public String profile() {
	return "profile";
    }

    @PostMapping("/transfer")
    public String transfer(@ModelAttribute("context") Context context,
	    @ModelAttribute("person") PersonMemory person) {
	person.getTransactions().add(new TransactionMemory(context.getTransfertCompteFrom(), context.getTransfertCompteTo(),
		context.getDescription(), context.getTransfertAmount()));
	return "transfer";
    }

    @PostMapping("/addCompteTo")
    public String addCompteTo(@ModelAttribute("context") Context context,
	    @ModelAttribute("person") PersonMemory person) {
	person.getComptesTo().add(new CompteMemory(context.getNewCompteTo()));
	return "contact";
    }

    @PostMapping("/deleteCompteTo")
    public String deleteCompteTo(@ModelAttribute("context") Context context,
	    @ModelAttribute("person") PersonMemory person) {

	for (CompteMemory compteTo : person.getComptesTo()) {
	    if (compteTo.getEmail().equals(context.getTransfertCompteTo())) {
		person.getComptesTo().remove(compteTo);
		break;
	    }
	}
	return "contact";
    }
}
