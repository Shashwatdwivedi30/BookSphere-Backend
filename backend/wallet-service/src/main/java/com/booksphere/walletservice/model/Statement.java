package com.booksphere.walletservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statement {
    private String transactionType; // "CREDIT" or "DEBIT"
    private Double amount;
    private LocalDateTime timestamp;
}
