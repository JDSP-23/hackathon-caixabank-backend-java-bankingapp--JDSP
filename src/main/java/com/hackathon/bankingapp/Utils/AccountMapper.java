package com.hackathon.bankingapp.Utils;

import com.hackathon.bankingapp.DTO.AccountDTO;
import com.hackathon.bankingapp.Entities.Account;

public class AccountMapper {

    public static AccountDTO toAccountDTO(Account account) {
        if (account == null) return null;
        return new AccountDTO(account.getAccountNumber(), account.getBalance());
    }

    public static Account toAccountEntity(AccountDTO accountDTO) {
        if (accountDTO == null) return null;
        Account account = new Account();
        account.setAccountNumber(accountDTO.getAccountNumber());
        account.setBalance(accountDTO.getBalance());
        return account;
    }
}
