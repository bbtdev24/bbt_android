package com.project.bbt.model;

public class PaymentModel {
    private String noUrut;
    private String namaKaryawan;
    private String lokasiHrd;
    private String jabatan;
    private String divisi;
    private String bagian;
    private String jenisKelamin;
    private String tanggalJoin;
    private String npwp;
    private String golongan;
    private String noRek;
    private String namaRek;
    private String digit_ktp;
    private String digit_npwp;
    private String digit_bpjs_ket;
    private String digit_bpjs_kes;
    private String gajiPokok;
    private String allowanceProject;
    private String tunjFungsional;
    private String tunjJabatan;
    private String tunjKinerja;
    private String tunjKomunikasi;
    private String tunjTransportasiAllowance;
    private String bonus;
    private String asuransiKesAllowance;
    private String perjalananDinasAllowance;
    private String jkkJkmJpkAllowance;
    private String jhtcAllowance;
    private String bpjsByCompanyAllowance;
    private String jpByCompanyAllowance;
    private String jhteAllowance;
    private String jpByEmployeeAllowance;
    private String bpjsByEmployeeAllowance;
    private String telatDeduction;
    private String absenDeduction;
    private String tunjTransportasiDeduction;
    private String asuransiKesDeduction;
    private String perjalananDinasDeduction;
    private String jkkJkmJpkDeduction;
    private String jhtcDeduction;
    private String bpjsByCompanyDeduction;
    private String jpByCompanyDeduction;
    private String jhteDeduction;
    private String jpByEmployeeDeduction;
    private String bpjsByEmployeeDeduction;
    private String thp;
    private String bruto;
    private String tax;
    private String bulan;
    private String tahun;

    // Constructor lengkap dengan semua data
    public PaymentModel(
            String noUrut, String namaKaryawan, String lokasiHrd, String jabatan, String divisi,
            String bagian, String jenisKelamin, String tanggalJoin, String npwp, String golongan,
            String noRek, String namaRek, String digit_ktp, String digit_npwp, String digit_bpjs_ket,
            String digit_bpjs_kes, String gajiPokok, String allowanceProject, String tunjFungsional,
            String tunjJabatan, String tunjKinerja, String tunjKomunikasi, String tunjTransportasiAllowance,
            String bonus, String asuransiKesAllowance, String perjalananDinasAllowance, String jkkJkmJpkAllowance,
            String jhtcAllowance, String bpjsByCompanyAllowance, String jpByCompanyAllowance, String jhteAllowance,
            String jpByEmployeeAllowance, String bpjsByEmployeeAllowance, String telatDeduction, String absenDeduction,
            String tunjTransportasiDeduction, String asuransiKesDeduction, String perjalananDinasDeduction,
            String jkkJkmJpkDeduction, String jhtcDeduction, String bpjsByCompanyDeduction, String jpByCompanyDeduction,
            String jhteDeduction, String jpByEmployeeDeduction, String bpjsByEmployeeDeduction,
            String thp, String bruto, String tax, String bulan, String tahun) {
        this.noUrut = noUrut;
        this.namaKaryawan = namaKaryawan;
        this.lokasiHrd = lokasiHrd;
        this.jabatan = jabatan;
        this.divisi = divisi;
        this.bagian = bagian;
        this.jenisKelamin = jenisKelamin;
        this.tanggalJoin = tanggalJoin;
        this.npwp = npwp;
        this.golongan = golongan;
        this.noRek = noRek;
        this.namaRek = namaRek;
        this.digit_ktp = digit_ktp;
        this.digit_npwp = digit_npwp;
        this.digit_bpjs_ket = digit_bpjs_ket;
        this.digit_bpjs_kes = digit_bpjs_kes;
        this.gajiPokok = gajiPokok;
        this.allowanceProject = allowanceProject;
        this.tunjFungsional = tunjFungsional;
        this.tunjJabatan = tunjJabatan;
        this.tunjKinerja = tunjKinerja;
        this.tunjKomunikasi = tunjKomunikasi;
        this.tunjTransportasiAllowance = tunjTransportasiAllowance;
        this.bonus = bonus;
        this.asuransiKesAllowance = asuransiKesAllowance;
        this.perjalananDinasAllowance = perjalananDinasAllowance;
        this.jkkJkmJpkAllowance = jkkJkmJpkAllowance;
        this.jhtcAllowance = jhtcAllowance;
        this.bpjsByCompanyAllowance = bpjsByCompanyAllowance;
        this.jpByCompanyAllowance = jpByCompanyAllowance;
        this.jhteAllowance = jhteAllowance;
        this.jpByEmployeeAllowance = jpByEmployeeAllowance;
        this.bpjsByEmployeeAllowance = bpjsByEmployeeAllowance;
        this.telatDeduction = telatDeduction;
        this.absenDeduction = absenDeduction;
        this.tunjTransportasiDeduction = tunjTransportasiDeduction;
        this.asuransiKesDeduction = asuransiKesDeduction;
        this.perjalananDinasDeduction = perjalananDinasDeduction;
        this.jkkJkmJpkDeduction = jkkJkmJpkDeduction;
        this.jhtcDeduction = jhtcDeduction;
        this.bpjsByCompanyDeduction = bpjsByCompanyDeduction;
        this.jpByCompanyDeduction = jpByCompanyDeduction;
        this.jhteDeduction = jhteDeduction;
        this.jpByEmployeeDeduction = jpByEmployeeDeduction;
        this.bpjsByEmployeeDeduction = bpjsByEmployeeDeduction;
        this.thp = thp;
        this.bruto = bruto;
        this.tax = tax;
        this.bulan = bulan;
        this.tahun = tahun;
    }

    public String getNoUrut() {
        return noUrut;
    }

    public void setNoUrut(String noUrut) {
        this.noUrut = noUrut;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }

    public String getLokasiHrd() {
        return lokasiHrd;
    }

    public void setLokasiHrd(String lokasiHrd) {
        this.lokasiHrd = lokasiHrd;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getDivisi() {
        return divisi;
    }

    public void setDivisi(String divisi) {
        this.divisi = divisi;
    }

    public String getBagian() {
        return bagian;
    }

    public void setBagian(String bagian) {
        this.bagian = bagian;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getTanggalJoin() {
        return tanggalJoin;
    }

    public void setTanggalJoin(String tanggalJoin) {
        this.tanggalJoin = tanggalJoin;
    }

    public String getNpwp() {
        return npwp;
    }

    public void setNpwp(String npwp) {
        this.npwp = npwp;
    }

    public String getGolongan() {
        return golongan;
    }

    public void setGolongan(String golongan) {
        this.golongan = golongan;
    }

    public String getNoRek() {
        return noRek;
    }

    public void setNoRek(String noRek) {
        this.noRek = noRek;
    }

    public String getNamaRek() {
        return namaRek;
    }

    public void setNamaRek(String namaRek) {
        this.namaRek = namaRek;
    }

    public String getDigit_ktp() {
        return digit_ktp;
    }

    public void setDigit_ktp(String digit_ktp) {
        this.digit_ktp = digit_ktp;
    }

    public String getDigit_npwp() {
        return digit_npwp;
    }

    public void setDigit_npwp(String digit_npwp) {
        this.digit_npwp = digit_npwp;
    }

    public String getDigit_bpjs_ket() {
        return digit_bpjs_ket;
    }

    public void setDigit_bpjs_ket(String digit_bpjs_ket) {
        this.digit_bpjs_ket = digit_bpjs_ket;
    }

    public String getDigit_bpjs_kes() {
        return digit_bpjs_kes;
    }

    public void setDigit_bpjs_kes(String digit_bpjs_kes) {
        this.digit_bpjs_kes = digit_bpjs_kes;
    }

    public String getGajiPokok() {
        return gajiPokok;
    }

    public void setGajiPokok(String gajiPokok) {
        this.gajiPokok = gajiPokok;
    }

    public String getAllowanceProject() {
        return allowanceProject;
    }

    public void setAllowanceProject(String allowanceProject) {
        this.allowanceProject = allowanceProject;
    }

    public String getTunjFungsional() {
        return tunjFungsional;
    }

    public void setTunjFungsional(String tunjFungsional) {
        this.tunjFungsional = tunjFungsional;
    }

    public String getTunjJabatan() {
        return tunjJabatan;
    }

    public void setTunjJabatan(String tunjJabatan) {
        this.tunjJabatan = tunjJabatan;
    }

    public String getTunjKinerja() {
        return tunjKinerja;
    }

    public void setTunjKinerja(String tunjKinerja) {
        this.tunjKinerja = tunjKinerja;
    }

    public String getTunjKomunikasi() {
        return tunjKomunikasi;
    }

    public void setTunjKomunikasi(String tunjKomunikasi) {
        this.tunjKomunikasi = tunjKomunikasi;
    }

    public String getTunjTransportasiAllowance() {
        return tunjTransportasiAllowance;
    }

    public void setTunjTransportasiAllowance(String tunjTransportasiAllowance) {
        this.tunjTransportasiAllowance = tunjTransportasiAllowance;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getAsuransiKesAllowance() {
        return asuransiKesAllowance;
    }

    public void setAsuransiKesAllowance(String asuransiKesAllowance) {
        this.asuransiKesAllowance = asuransiKesAllowance;
    }

    public String getPerjalananDinasAllowance() {
        return perjalananDinasAllowance;
    }

    public void setPerjalananDinasAllowance(String perjalananDinasAllowance) {
        this.perjalananDinasAllowance = perjalananDinasAllowance;
    }

    public String getJkkJkmJpkAllowance() {
        return jkkJkmJpkAllowance;
    }

    public void setJkkJkmJpkAllowance(String jkkJkmJpkAllowance) {
        this.jkkJkmJpkAllowance = jkkJkmJpkAllowance;
    }

    public String getJhtcAllowance() {
        return jhtcAllowance;
    }

    public void setJhtcAllowance(String jhtcAllowance) {
        this.jhtcAllowance = jhtcAllowance;
    }

    public String getBpjsByCompanyAllowance() {
        return bpjsByCompanyAllowance;
    }

    public void setBpjsByCompanyAllowance(String bpjsByCompanyAllowance) {
        this.bpjsByCompanyAllowance = bpjsByCompanyAllowance;
    }

    public String getJpByCompanyAllowance() {
        return jpByCompanyAllowance;
    }

    public void setJpByCompanyAllowance(String jpByCompanyAllowance) {
        this.jpByCompanyAllowance = jpByCompanyAllowance;
    }

    public String getJhteAllowance() {
        return jhteAllowance;
    }

    public void setJhteAllowance(String jhteAllowance) {
        this.jhteAllowance = jhteAllowance;
    }

    public String getJpByEmployeeAllowance() {
        return jpByEmployeeAllowance;
    }

    public void setJpByEmployeeAllowance(String jpByEmployeeAllowance) {
        this.jpByEmployeeAllowance = jpByEmployeeAllowance;
    }

    public String getBpjsByEmployeeAllowance() {
        return bpjsByEmployeeAllowance;
    }

    public void setBpjsByEmployeeAllowance(String bpjsByEmployeeAllowance) {
        this.bpjsByEmployeeAllowance = bpjsByEmployeeAllowance;
    }

    public String getTelatDeduction() {
        return telatDeduction;
    }

    public void setTelatDeduction(String telatDeduction) {
        this.telatDeduction = telatDeduction;
    }

    public String getAbsenDeduction() {
        return absenDeduction;
    }

    public void setAbsenDeduction(String absenDeduction) {
        this.absenDeduction = absenDeduction;
    }

    public String getTunjTransportasiDeduction() {
        return tunjTransportasiDeduction;
    }

    public void setTunjTransportasiDeduction(String tunjTransportasiDeduction) {
        this.tunjTransportasiDeduction = tunjTransportasiDeduction;
    }

    public String getAsuransiKesDeduction() {
        return asuransiKesDeduction;
    }

    public void setAsuransiKesDeduction(String asuransiKesDeduction) {
        this.asuransiKesDeduction = asuransiKesDeduction;
    }

    public String getPerjalananDinasDeduction() {
        return perjalananDinasDeduction;
    }

    public void setPerjalananDinasDeduction(String perjalananDinasDeduction) {
        this.perjalananDinasDeduction = perjalananDinasDeduction;
    }

    public String getJkkJkmJpkDeduction() {
        return jkkJkmJpkDeduction;
    }

    public void setJkkJkmJpkDeduction(String jkkJkmJpkDeduction) {
        this.jkkJkmJpkDeduction = jkkJkmJpkDeduction;
    }

    public String getJhtcDeduction() {
        return jhtcDeduction;
    }

    public void setJhtcDeduction(String jhtcDeduction) {
        this.jhtcDeduction = jhtcDeduction;
    }

    public String getBpjsByCompanyDeduction() {
        return bpjsByCompanyDeduction;
    }

    public void setBpjsByCompanyDeduction(String bpjsByCompanyDeduction) {
        this.bpjsByCompanyDeduction = bpjsByCompanyDeduction;
    }

    public String getJpByCompanyDeduction() {
        return jpByCompanyDeduction;
    }

    public void setJpByCompanyDeduction(String jpByCompanyDeduction) {
        this.jpByCompanyDeduction = jpByCompanyDeduction;
    }

    public String getJhteDeduction() {
        return jhteDeduction;
    }

    public void setJhteDeduction(String jhteDeduction) {
        this.jhteDeduction = jhteDeduction;
    }

    public String getJpByEmployeeDeduction() {
        return jpByEmployeeDeduction;
    }

    public void setJpByEmployeeDeduction(String jpByEmployeeDeduction) {
        this.jpByEmployeeDeduction = jpByEmployeeDeduction;
    }

    public String getBpjsByEmployeeDeduction() {
        return bpjsByEmployeeDeduction;
    }

    public void setBpjsByEmployeeDeduction(String bpjsByEmployeeDeduction) {
        this.bpjsByEmployeeDeduction = bpjsByEmployeeDeduction;
    }

    public String getThp() {
        return thp;
    }

    public void setThp(String thp) {
        this.thp = thp;
    }

    public String getBruto() {
        return bruto;
    }

    public void setBruto(String bruto) {
        this.bruto = bruto;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getBulan() {
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }
}
