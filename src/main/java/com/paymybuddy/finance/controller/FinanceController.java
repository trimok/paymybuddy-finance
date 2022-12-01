package com.paymybuddy.finance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.paymybuddy.finance.model.Compte;
import com.paymybuddy.finance.model.Context;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.model.User;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@SessionAttributes(value = { "context", "user" })
public class FinanceController {

    @GetMapping(value = { "/", "/login" })
    public String login(Model model) {
	model.addAttribute("context", new Context());
	model.addAttribute("user", new User());
	return "login";
    }

    @PostMapping("/login")
    public String loginControl(Model model, @ModelAttribute("user") User user) {
	User userByModel = (User) model.getAttribute("user");
	log.info(userByModel.toString());
	model.addAttribute("comptes", Compte.getComptes());
	user.getComptesFrom().add(new Compte("Buddy : " + user.getEmail()));
	user.getComptesFrom().add(new Compte("BNP : " + user.getEmail()));
	user.getComptesTo().add(new Compte("Buddy : " + user.getEmail()));
	user.getComptesTo().add(new Compte("BNP : " + user.getEmail()));
	return "transfert";
    }

    @PostMapping("/transfert")
    public String transfert(Model model, @ModelAttribute("user") User user,
	    @ModelAttribute("context") Context context) {
	User userByModel = (User) model.getAttribute("user");
	user.getTransactions().add(new Transaction(context.getTransfertCompteFrom(), context.getTransfertCompteTo(),
		context.getDescription(), context.getTransfertAmount()));
	log.info(userByModel.toString());
	return "transfert";
    }

    @PostMapping("/addCompteTo")
    public String addConnection(Model model, @ModelAttribute("user") User user,
	    @ModelAttribute("context") Context context) {
	User userByModel = (User) model.getAttribute("user");
	user.getComptesTo().add(new Compte(context.getNewCompteTo()));
	log.info(userByModel.toString());
	return "transfert";
    }
}
