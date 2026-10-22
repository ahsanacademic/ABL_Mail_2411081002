package com.asan.mail.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.asan.mail.model.OrderMessage;

@ExtendWith(MockitoExtension.class)
class OrderMailSenderTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void createdEventSendsCreationConfirmation() {
        sendAndAssert(OrderMessage.ORDER_CREATED, "Konfirmasi Order #71", "Order Anda berhasil dibuat.");
    }

    @Test
    void updatedEventSendsUpdateNoticeNotCreationConfirmation() {
        sendAndAssert(OrderMessage.ORDER_UPDATED, "Pembaruan Order #71", "Order Anda telah diperbarui.");
    }

    @Test
    void deletedEventSendsCancellationNotice() {
        sendAndAssert(OrderMessage.ORDER_DELETED, "Pembatalan Order #71", "Order Anda telah dibatalkan.");
    }

    @Test
    void eventWithoutTypeFromAnOlderQueuedMessageDefaultsToCreation() {
        sendAndAssert(null, "Konfirmasi Order #71", "Order Anda berhasil dibuat.");
    }

    private void sendAndAssert(String eventType, String expectedSubject, String expectedStatusText) {
        OrderMailSender sender = new OrderMailSender(mailSender, "sender@example.test");
        OrderMessage event = orderEvent(eventType);

        sender.sendOrderMail(event);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sent = messageCaptor.getValue();
        assertEquals(expectedSubject, sent.getSubject());
        assertTrue(sent.getText().contains(expectedStatusText));
        assertTrue(sent.getText().contains("Kopi Susu"));
        assertTrue(sent.getText().contains("Siti"));
    }

    private OrderMessage orderEvent(String eventType) {
        OrderMessage event = new OrderMessage();
        event.setEventType(eventType);
        event.setId(71L);
        event.setProdukId(3L);
        event.setProdukNama("Kopi Susu");
        event.setProdukHarga(25_000.0);
        event.setPelangganId(42L);
        event.setPelangganNama("Siti");
        event.setPelangganAlamat("Jl. Contoh");
        event.setEmail("siti@example.test");
        event.setTglTransaksi(LocalDate.of(2026, 10, 22));
        event.setJumlah(2);
        event.setTotal(50_000.0);
        return event;
    }
}
