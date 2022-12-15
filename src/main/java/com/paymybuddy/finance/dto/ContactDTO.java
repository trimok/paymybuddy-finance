package com.paymybuddy.finance.dto;

import java.util.List;

import com.paymybuddy.finance.model.Account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author trimok
 *
 */
@Getter
@Setter
/**
 * @NoArgsConstructor
 */
@NoArgsConstructor
/**
 * @param allAccounts              : all possible contact accounts
 * @param contactAccountIdToAdd    : id of the contact to add
 * @param contactAccountIdToRemove :id of the contact to remove
 */
@AllArgsConstructor
public class ContactDTO {

    /**
     * all possible contact accounts
     */
    /**
     * 
     */
    private List<Account> allAccounts;

    /**
     * id of the contact to add
     */
    /**
     * 
     */
    private Long contactAccountIdToAdd;
    /**
     * id of the contact to remove
     */
    /**
     * 
     */
    private Long contactAccountIdToRemove;
}
