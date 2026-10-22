package com.asan.mail.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderMessage {

    public static final String ORDER_CREATED = "ORDER_CREATED";
    public static final String ORDER_UPDATED = "ORDER_UPDATED";
    public static final String ORDER_DELETED = "ORDER_DELETED";

    private String eventType;
    private Long id;
    private Long produkId;
    private String produkNama;
    private Double produkHarga;
    private Long pelangganId;
    private String pelangganNama;
    private String pelangganAlamat;
    private String email;
    private LocalDate tglTransaksi;
    private int jumlah;
    private double total;
}
