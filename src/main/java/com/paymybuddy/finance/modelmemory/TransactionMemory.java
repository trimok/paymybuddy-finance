package com.paymybuddy.finance.modelmemory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionMemory {
    private String emailFrom;
    private String emailTo;
    private String description;
    private int amount;
}
