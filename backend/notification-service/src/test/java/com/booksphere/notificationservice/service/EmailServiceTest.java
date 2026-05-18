package com.booksphere.notificationservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendEmail_Success() {
        emailService.sendEmail("to@example.com", "Sub", "Body");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_Exception() {
        doThrow(new RuntimeException("Mail Error")).when(mailSender).send(any(SimpleMailMessage.class));
        emailService.sendEmail("to@example.com", "Sub", "Body");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
