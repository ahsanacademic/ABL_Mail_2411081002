package com.asan.mail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.asan.mail.model.OrderMessage;

@Service
public class OrderMailSender {

    private static final Logger logger = LoggerFactory.getLogger(OrderMailSender.class);

    private final JavaMailSender javaMailSender;
    private final String from;

    public OrderMailSender(JavaMailSender javaMailSender, @Value("${spring.mail.username}") String from) {
        this.javaMailSender = javaMailSender;
        this.from = from;
    }

    public void sendOrderMail(OrderMessage order) {
        String to = order.getEmail();
        if (to == null || to.isBlank()) {
            logger.warn("Skipping order email with missing recipient; orderId={}", order.getId());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subjectFor(order));
        message.setText(buildBody(order));
        javaMailSender.send(message);
        logger.info("Order email sent; eventType={}, orderId={}", normalizedEventType(order), order.getId());
    }

    private String subjectFor(OrderMessage order) {
        String number = String.valueOf(order.getId());
        return switch (normalizedEventType(order)) {
            case OrderMessage.ORDER_UPDATED -> "Pembaruan Order #" + number;
            case OrderMessage.ORDER_DELETED -> "Pembatalan Order #" + number;
            case OrderMessage.ORDER_CREATED -> "Konfirmasi Order #" + number;
            default -> "Perubahan Order #" + number;
        };
    }

    private String buildBody(OrderMessage order) {
        String customerName = hasText(order.getPelangganNama()) ? order.getPelangganNama() : "Pelanggan";
        String productName = hasText(order.getProdukNama())
                ? order.getProdukNama()
                : "Produk #" + order.getProdukId();
        String statusText = switch (normalizedEventType(order)) {
            case OrderMessage.ORDER_UPDATED -> "Order Anda telah diperbarui.";
            case OrderMessage.ORDER_DELETED -> "Order Anda telah dibatalkan.";
            case OrderMessage.ORDER_CREATED -> "Order Anda berhasil dibuat.";
            default -> "Status order Anda telah berubah.";
        };

        StringBuilder body = new StringBuilder()
                .append("Halo ").append(customerName).append(",\n\n")
                .append(statusText).append("\n\n")
                .append("No. Order    : ").append(order.getId()).append("\n")
                .append("Tanggal      : ").append(order.getTglTransaksi()).append("\n")
                .append("Nama Produk  : ").append(productName).append("\n");

        if (order.getProdukHarga() != null) {
            body.append("Harga Produk : ").append(order.getProdukHarga()).append("\n");
        }
        body.append("Jumlah       : ").append(order.getJumlah()).append("\n")
                .append("Total        : ").append(order.getTotal()).append("\n");

        if (hasText(order.getPelangganAlamat())) {
            body.append("Alamat Kirim : ").append(order.getPelangganAlamat()).append("\n");
        }

        return body.append("\nTerima kasih.\n\nSalam,\nPangeran").toString();
    }

    private String normalizedEventType(OrderMessage order) {
        String eventType = order.getEventType();
        return hasText(eventType) ? eventType : OrderMessage.ORDER_CREATED;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
