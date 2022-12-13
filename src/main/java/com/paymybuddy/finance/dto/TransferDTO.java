package com.paymybuddy.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    Long accountFromId;
    Long accountToId;
    String description;
    double amount;

    public TransferDTO(Long accountFromId, Long accountToId) {
	super();
	this.accountFromId = accountFromId;
	this.accountToId = accountToId;
    }
}