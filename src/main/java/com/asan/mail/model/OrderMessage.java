package com.asan.mail.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderMessage {

    private Long id;
    private Long produkId;
    private Long pelangganId;
    private String email;
    private LocalDate tglTransaksi;
    private int jumlah;
    private double total;
}
