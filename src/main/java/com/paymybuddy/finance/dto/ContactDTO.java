package com.paymybuddy.finance.dto;

import java.util.List;

import com.paymybuddy.finance.model.Account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {

    private List<Account> allAccounts;

    private Long contactAccountIdToAdd;
    private Long contactAccountIdToRemove;
}
