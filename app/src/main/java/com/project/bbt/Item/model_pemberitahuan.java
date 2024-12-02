package com.project.bbt.Item;

public class model_pemberitahuan {
    String title_training,
            keterangan_training,
            file_path,
            tanggal_pengajuan,
            kode_training;

    public model_pemberitahuan(String title_training, String keterangan_training, String file_path, String tanggal_pengajuan, String kode_training) {
        this.title_training = title_training;
        this.keterangan_training = keterangan_training;
        this.file_path = file_path;
        this.tanggal_pengajuan = tanggal_pengajuan;
        this.kode_training = kode_training;
    }

    public String getTitle_training() {
        return title_training;
    }

    public void setTitle_training(String title_training) {
        this.title_training = title_training;
    }

    public String getKeterangan_training() {
        return keterangan_training;
    }

    public void setKeterangan_training(String keterangan_training) {
        this.keterangan_training = keterangan_training;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getTanggal_pengajuan() {
        return tanggal_pengajuan;
    }

    public void setTanggal_pengajuan(String tanggal_pengajuan) {
        this.tanggal_pengajuan = tanggal_pengajuan;
    }

    public String getKode_training() {
        return kode_training;
    }

    public void setKode_training(String kode_training) {
        this.kode_training = kode_training;
    }

    private boolean isRead;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}

