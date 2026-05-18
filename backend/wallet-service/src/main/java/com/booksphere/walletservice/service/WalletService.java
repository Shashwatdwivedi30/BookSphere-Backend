package com.booksphere.walletservice.service;

import com.booksphere.walletservice.model.Statement;
import com.booksphere.walletservice.model.Wallet;

import java.util.List;

public interface WalletService {
    Wallet addMoney(String walletId, Double amount);
    Wallet pay(String walletId, Double amount);
    Wallet getWallet(String walletId);
    List<Statement> getStatements(String walletId);
}
