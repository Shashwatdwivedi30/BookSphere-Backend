package com.booksphere.walletservice.service;

import com.booksphere.walletservice.model.Wallet;
import com.booksphere.walletservice.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    void testGetWallet() {
        Wallet wallet = new Wallet();
        wallet.setWalletId("test@example.com");
        wallet.setCurrentBalance(100.0);
        when(walletRepository.findById("test@example.com")).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWallet("test@example.com");

        assertEquals(100.0, result.getCurrentBalance());
    }

    @Test
    void testAddMoney() {
        Wallet wallet = new Wallet();
        wallet.setWalletId("test@example.com");
        wallet.setCurrentBalance(100.0);
        when(walletRepository.findById("test@example.com")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet result = walletService.addMoney("test@example.com", 50.0);

        assertEquals(150.0, result.getCurrentBalance());
    }

    @Test
    void testDeductMoney_Success() {
        Wallet wallet = new Wallet();
        wallet.setWalletId("test@example.com");
        wallet.setCurrentBalance(100.0);
        when(walletRepository.findById("test@example.com")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet result = walletService.pay("test@example.com", 50.0);

        assertEquals(50.0, result.getCurrentBalance());
    }

    @Test
    void testDeductMoney_InsufficientFunds() {
        Wallet wallet = new Wallet();
        wallet.setWalletId("test@example.com");
        wallet.setCurrentBalance(10.0);
        when(walletRepository.findById("test@example.com")).thenReturn(Optional.of(wallet));

        org.junit.jupiter.api.Assertions.assertThrows(com.booksphere.walletservice.exception.InsufficientFundsException.class, () -> walletService.pay("test@example.com", 50.0));
    }

    @Test
    void testGetStatements() {
        Wallet wallet = new Wallet();
        wallet.setWalletId("test@example.com");
        when(walletRepository.findById("test@example.com")).thenReturn(Optional.of(wallet));
        walletService.getStatements("test@example.com");
        org.mockito.Mockito.verify(walletRepository).findById("test@example.com");
    }
}
