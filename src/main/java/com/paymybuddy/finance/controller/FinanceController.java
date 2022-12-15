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

/**
 * @author trimok
 *
 */
@Controller
@SessionAttributes(value = { "person" })
public class FinanceController {

    /**
     * The finance service
     */
    @Autowired
    private IFinanceService financeService;

    /**
     * The person service
     */
    @Autowired
    private IPersonService personService;

    /**
     * The account service
     */
    @Autowired
    private IAccountService accountService;

    /**
     * @return the home page
     */
    @PostMapping("/gotoHome")
    public String home() {
	return "home";
    }

    /**
     * @return th provile page
     */
    @PostMapping("/gotoProfile")
    public String profile() {
	return "profile";
    }

    /**
     * Initialisations for transfer page
     * 
     * @param model       : the model
     * @param person      : the person
     * @param transferDTO : the transfer DTO
     */
    private void initModelTransfer(Model model, Person person, TransferDTO transferDTO) {
	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);

	model.addAttribute("transferDTO", transferDTO);
    }

    /**
     * @param model       : the model
     * @param person      : the person
     * @param transferDTO : the transfer DTO
     * @return : the transfer page
     */
    @PostMapping("/gotoTransfer")
    public String goToTransfer(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("transferDTO") TransferDTO transferDTO) {

	initModelTransfer(model, person, transferDTO);

	return "transfer";
    }

    /**
     * Transaction operation
     * 
     * @param model       : the model
     * @param person      : the person
     * @param transferDTO : the transfer DTO
     * @return : the transfer page
     */
    @PostMapping("/transfer")
    public String transfer(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("transferDTO") TransferDTO transferDTO) {

	List<String> errors = financeService.validateCreateTransaction(person, transferDTO);
	if (errors.isEmpty()) {
	    financeService.createTransaction(person, transferDTO);
	} else {
	    errors.forEach(e -> model.addAttribute(e, true));
	}

	initModelTransfer(model, person, transferDTO);

	return "transfer";
    }

    /**
     * Initializations for contact page
     *
     * @param model      : the model
     * @param person     : the person
     * @param contactDTO : the contactDTO
     */
    private void initModelContact(Model model, Person person, ContactDTO contactDTO) {
	person = personService.findFetchWithAllPersonByName(person.getName());
	model.addAttribute("person", person);

	List<Account> accounts = accountService.findAllAccountsExceptPersonAccounts(person);
	accounts.removeAll(person.getContactAccounts());
	model.addAttribute("contactDTO", contactDTO);
	contactDTO.setAllAccounts(accounts);
    }

    /**
     * @param model      : the model
     * @param person     : the person
     * @param contactDTO : the contactDTO
     * @return : the contact page
     */
    @PostMapping("/gotoContact")
    public String goToContact(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("contactDTO") ContactDTO contactDTO) {

	initModelContact(model, person, contactDTO);

	return "contact";
    }

    /**
     * Adding a contact account
     * 
     * @param model      : the model
     * @param person     : the person
     * @param contactDTO : the contactDTO
     * @return : the contact page
     */
    @PostMapping("/addContactAccount")
    public String addContactAccountTo(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("contactDTO") ContactDTO contactDTO) {

	List<String> errors = accountService.validateCreateContactAccount(person, contactDTO);
	if (errors.isEmpty()) {
	    accountService.createContactAccount(person, contactDTO);
	} else {
	    errors.forEach(e -> model.addAttribute(e, true));
	}

	initModelContact(model, person, contactDTO);

	return "contact";
    }

    /**
     * Removing a contact account
     * 
     * @param model      : the model
     * @param person     : the person
     * @param contactDTO : the contactDTO
     * @return : the contact page
     */

    @PostMapping("/removeContactAccount")
    public String removeContactAccount(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("contactDTO") ContactDTO contactDTO) {

	List<String> errors = accountService.validateRemoveContactAccount(person, contactDTO);
	if (errors.isEmpty()) {
	    accountService.removeContactAccount(person, contactDTO);
	} else {
	    errors.forEach(e -> model.addAttribute(e, true));
	}

	initModelContact(model, person, contactDTO);

	return "contact";
    }
}
