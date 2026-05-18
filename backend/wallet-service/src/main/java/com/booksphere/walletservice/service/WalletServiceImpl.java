package com.booksphere.walletservice.service;

import com.booksphere.walletservice.exception.InsufficientFundsException;
import com.booksphere.walletservice.model.Statement;
import com.booksphere.walletservice.model.Wallet;
import com.booksphere.walletservice.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private static final String TYPE_CREDIT = "CREDIT";
    private static final String TYPE_DEBIT = "DEBIT";

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet addMoney(String walletId, Double amount) {
        log.info("Adding {} to wallet {}", amount, walletId);
        Wallet wallet = getOrCreateWallet(walletId);
        wallet.setCurrentBalance(wallet.getCurrentBalance() + amount);
        wallet.getStatements().add(new Statement(TYPE_CREDIT, amount, LocalDateTime.now()));
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet pay(String walletId, Double amount) {
        log.info("Paying {} from wallet {}", amount, walletId);
        Wallet wallet = getOrCreateWallet(walletId);
        
        if (wallet.getCurrentBalance() < amount) {
            log.error("Insufficient funds in wallet {}. Required: {}, Available: {}", walletId, amount, wallet.getCurrentBalance());
            throw new InsufficientFundsException("Insufficient funds in wallet");
        }
        
        wallet.setCurrentBalance(wallet.getCurrentBalance() - amount);
        wallet.getStatements().add(new Statement(TYPE_DEBIT, amount, LocalDateTime.now()));
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet getWallet(String walletId) {
        return getOrCreateWallet(walletId);
    }

    @Override
    public List<Statement> getStatements(String walletId) {
        return getOrCreateWallet(walletId).getStatements();
    }

    private Wallet getOrCreateWallet(String walletId) {
        return walletRepository.findById(walletId).orElseGet(() -> {
            log.info("Creating new wallet for {}", walletId);
            Wallet newWallet = new Wallet();
            newWallet.setWalletId(walletId);
            newWallet.setCurrentBalance(0.0);
            return walletRepository.save(newWallet);
        });
    }
}
