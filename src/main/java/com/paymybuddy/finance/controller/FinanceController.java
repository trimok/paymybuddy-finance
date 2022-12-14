package com.paymybuddy.finance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.paymybuddy.finance.dto.ContactDTO;
import com.paymybuddy.finance.dto.TransferDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.service.IAccountService;
import com.paymybuddy.finance.service.IFinanceService;
import com.paymybuddy.finance.service.IPersonService;

@Controller
@SessionAttributes(value = { "person" })
public class FinanceController {

    @Autowired
    private IFinanceService financeService;

    @Autowired
    private IPersonService personService;

    @Autowired
    private IAccountService accountService;

    @PostMapping("/gotoHome")
    public String home() {
	return "home";
    }

    @PostMapping("/gotoTransfer")
    public String transfer(Model model, @ModelAttribute("person") Person person) {
	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);
	model.addAttribute("transferDTO", new TransferDTO());
	return "transfer";
    }

    @PostMapping("/gotoContact")
    public String contact(Model model, @ModelAttribute("person") Person person) {

	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);

	List<Account> accounts = accountService.findAllAccountsExceptPersonAccounts(person);
	accounts.removeAll(person.getContactAccounts());
	ContactDTO contactDTO = new ContactDTO();
	model.addAttribute("contactDTO", contactDTO);
	contactDTO.setAllAccounts(accounts);

	return "contact";
    }

    @PostMapping("/gotoProfile")
    public String profile() {
	return "profile";
    }

    @PostMapping("/transfer")
    public String transfer(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("transerDTO") TransferDTO transferDTO) {

	List<String> errors = financeService.validateCreateTransaction(person, transferDTO);
	if (errors.isEmpty()) {
	    financeService.createTransaction(person, transferDTO);
	} else {
	    errors.forEach(e -> model.addAttribute(e, true));
	}

	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);

	model.addAttribute("transferDTO", transferDTO);
	return "transfer";
    }

    @PostMapping("/addContactAccount")
    public String addContactAccountTo(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("contactDTO") ContactDTO contactDTO) {

	List<String> errors = accountService.validateCreateContactAccount(person, contactDTO);
	if (errors.isEmpty()) {
	    accountService.createContactAccount(person, contactDTO);
	} else {
	    errors.forEach(e -> model.addAttribute(e, true));
	}

	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);

	List<Account> accounts = accountService.findAllAccountsExceptPersonAccounts(person);
	accounts.removeAll(person.getContactAccounts());
	model.addAttribute("contactDTO", contactDTO);
	contactDTO.setAllAccounts(accounts);

	return "contact";
    }

    @PostMapping("/removeContactAccount")
    public String removeContactAccount(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("contactDTO") ContactDTO contactDTO) {

	List<String> errors = accountService.validateRemoveContactAccount(person, contactDTO);
	if (errors.isEmpty()) {
	    accountService.removeContactAccount(person, contactDTO);
	} else {
	    errors.forEach(e -> model.addAttribute(e, true));
	}

	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);

	List<Account> accounts = accountService.findAllAccountsExceptPersonAccounts(person);
	accounts.removeAll(person.getContactAccounts());
	model.addAttribute("contactDTO", contactDTO);
	contactDTO.setAllAccounts(accounts);

	return "contact";
    }
}
