package com.paymybuddy.finance.dto;

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
 * @param accountFromId : id of the origin account
 * @param accountToId   : id of the final account
 * @param description   : description
 * @param amount        : amount
 */
@AllArgsConstructor
public class TransferDTO {
    /**
     * id of the origin account
     */
    Long accountFromId;
    /**
     * id of the final account
     */
    Long accountToId;
    /**
     * description
     */
    String description;
    /**
     * amount
     */
    double amount;

    /**
     * @param accountFromId : id of the origin account
     * @param accountToId   : id of the final account
     */
    public TransferDTO(Long accountFromId, Long accountToId) {
	super();
	this.accountFromId = accountFromId;
	this.accountToId = accountToId;
    }
}