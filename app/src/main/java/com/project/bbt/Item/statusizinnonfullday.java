package com.project.bbt.Item;

public class statusizinnonfullday {
    private String lokasi_struktur;
    private String status_non_full;

    public statusizinnonfullday(String lokasi_struktur, String status_non_full){
        this.lokasi_struktur = lokasi_struktur;
        this.status_non_full = status_non_full;
    }
    public String getLokasi_struktur() {return lokasi_struktur; }
    public String getStatus_non_full() {
        return status_non_full;
    }
}

