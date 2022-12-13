package com.paymybuddy.finance.controller;

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
	ContactDTO contactDTO = new ContactDTO();
	contactDTO.setAllAccounts(accountService.findAllAccountsExceptPersonAccounts(person));
	model.addAttribute("contactDTO", contactDTO);

	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);

	return "contact";
    }

    @PostMapping("/gotoProfile")
    public String profile() {
	return "profile";
    }

    @PostMapping("/transfer")
    public String transfer(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("transerDTO") TransferDTO transferDTO) {
	boolean dataOk = true;
	if (transferDTO.getAccountFromId() == null) {
	    dataOk = false;
	    model.addAttribute("selectAccountFrom", true);
	} else if (transferDTO.getAccountToId() == null) {
	    dataOk = false;
	    model.addAttribute("selectAccountTo", true);
	} else if (transferDTO.getAccountToId() == transferDTO.getAccountFromId()) {
	    dataOk = false;
	    model.addAttribute("accountsMustBeDifferent", true);
	}

	if (dataOk) {
	    financeService.createTransaction(person, transferDTO);
	}

	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);

	model.addAttribute("transferDTO", transferDTO);
	return "transfer";
    }

    @PostMapping("/addCompteTo")
    public String addCompteTo(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("contactDTO") ContactDTO contactDTO) {

	boolean dataOk = true;
	if (contactDTO.getContactAccountIdToAdd() == null) {
	    dataOk = false;
	    model.addAttribute("selectAccountToAdd", true);
	}
	if (dataOk) {
	    Account accountToAdd = accountService.findAccountById(contactDTO.getContactAccountIdToAdd());
	    if (person.getContactAccounts().contains(accountToAdd)) {
		dataOk = false;
		model.addAttribute("accountAlreadyExists", true);
	    }
	}

	if (dataOk) {
	    accountService.createContactAccount(person, contactDTO);
	}

	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);

	contactDTO.setAllAccounts(accountService.findAllAccountsExceptPersonAccounts(person));
	model.addAttribute("contactDTO", contactDTO);
	return "contact";
    }

    @PostMapping("/deleteCompteTo")
    public String deleteCompteTo(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("contactDTO") ContactDTO contactDTO) {
	boolean dataOk = true;
	if (contactDTO.getContactAccountIdToRemove() == null) {
	    dataOk = false;
	    model.addAttribute("selectAccountToRemove", true);
	}

	if (dataOk) {
	    accountService.removeContactAccount(person, contactDTO);
	}

	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);

	contactDTO.setAllAccounts(accountService.findAllAccountsExceptPersonAccounts(person));
	model.addAttribute("contactDTO", contactDTO);

	return "contact";
    }
}
