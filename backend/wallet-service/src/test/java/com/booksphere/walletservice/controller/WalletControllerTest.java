package com.booksphere.walletservice.controller;

import com.booksphere.walletservice.model.Wallet;
import com.booksphere.walletservice.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @Test
    void testGetWallet() {
        when(walletService.getWallet(anyString())).thenReturn(new Wallet());
        assertNotNull(walletController.getWallet("u1"));
    }

    @Test
    void testAddMoney() {
        when(walletService.addMoney(anyString(), anyDouble())).thenReturn(new Wallet());
        assertNotNull(walletController.addMoney("u1", 10.0));
    }

    @Test
    void testPay() {
        when(walletService.pay(anyString(), anyDouble())).thenReturn(new Wallet());
        assertNotNull(walletController.pay("u1", 10.0));
    }

    @Test
    void testGetStatements() {
        when(walletService.getStatements(anyString())).thenReturn(new ArrayList<>());
        assertNotNull(walletController.getStatements("u1"));
    }
}
