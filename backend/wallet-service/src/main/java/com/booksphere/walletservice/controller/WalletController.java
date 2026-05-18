package com.booksphere.walletservice.controller;

import com.booksphere.walletservice.model.Statement;
import com.booksphere.walletservice.model.Wallet;
import com.booksphere.walletservice.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/addMoney")
    public Wallet addMoney(@RequestParam String walletId, @RequestParam Double amount) {
        return walletService.addMoney(walletId, amount);
    }

    @PostMapping("/pay")
    public Wallet pay(@RequestParam String walletId, @RequestParam Double amount) {
        return walletService.pay(walletId, amount);
    }

    @GetMapping("/{id:.+}")
    public Wallet getWallet(@PathVariable("id") String walletId) {
        return walletService.getWallet(walletId);
    }

    @GetMapping("/statements/{id:.+}")
    public List<Statement> getStatements(@PathVariable("id") String walletId) {
        return walletService.getStatements(walletId);
    }
}
