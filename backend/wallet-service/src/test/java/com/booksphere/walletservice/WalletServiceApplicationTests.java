package com.booksphere.walletservice;

import com.booksphere.walletservice.controller.WalletController;
import com.booksphere.walletservice.model.Wallet;
import com.booksphere.walletservice.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
class WalletServiceApplicationTests {

    @Autowired
    private WalletController walletController;

    @Autowired
    private WalletService walletService;

    @MockBean
    private com.booksphere.walletservice.repository.WalletRepository walletRepository;

    @Test
    void contextLoads() {
        assertThat(walletController).isNotNull();
        assertThat(walletService).isNotNull();
    }

    @Test
    void testWalletModelCreation() {
        Wallet wallet = new Wallet();
        wallet.setWalletId("user1@test.com");
        wallet.setCurrentBalance(500.0);
        assertThat(wallet.getCurrentBalance()).isEqualTo(500.0);
    }

    @Test
    void testWalletServiceBeanExists() {
        assertThat(walletService).isNotNull();
    }

    @Test
    void testWalletControllerBeanExists() {
        assertThat(walletController).isNotNull();
    }

    @Test
    void testGetBalanceIntegration() {
        Wallet wallet = new Wallet();
        wallet.setCurrentBalance(100.0);
        
        when(walletRepository.findById("user1")).thenReturn(java.util.Optional.of(wallet));
        
        Wallet result = walletController.getWallet("user1");
        assertThat(result.getCurrentBalance()).isEqualTo(100.0);
    }

    @Test
    void testWalletBalanceNonNegative() {
        Wallet wallet = new Wallet();
        wallet.setCurrentBalance(0.0);
        assertThat(wallet.getCurrentBalance()).isNotNegative();
    }

    @Test
    void testWalletUserEmailField() {
        Wallet wallet = new Wallet();
        wallet.setWalletId("test@booksphere.com");
        assertThat(wallet.getWalletId()).isEqualTo("test@booksphere.com");
    }

    @Test
    void testWalletTransactionSim() {
        Wallet wallet = new Wallet();
        wallet.setCurrentBalance(1000.0);
        // Simulation of a deduction
        wallet.setCurrentBalance(wallet.getCurrentBalance() - 100.0);
        assertThat(wallet.getCurrentBalance()).isEqualTo(900.0);
    }

    @Test
    void testWalletDepositSim() {
        Wallet wallet = new Wallet();
        wallet.setCurrentBalance(100.0);
        wallet.setCurrentBalance(wallet.getCurrentBalance() + 50.0);
        assertThat(wallet.getCurrentBalance()).isEqualTo(150.0);
    }
}
