package com.rentallaptop;

public abstract class Barang {

    private String id;
    private String nama;
    private double tarifHarian;
    private boolean tersedia;

    protected Barang(String id, String nama, double tarifHarian) throws InputTidakValidException {
        setId(id);
        setNama(nama);
        setTarifHarian(tarifHarian);
        this.tersedia = true;
    }

    public abstract double hitungBiaya(int hari);

    public abstract String getKategori();

    public String getId() {
        return id;
    }

    public void setId(String id) throws InputTidakValidException {
        if (id == null || id.trim().isEmpty()) {
            throw new InputTidakValidException("ID barang tidak boleh kosong.");
        }
        this.id = id.trim().toUpperCase();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) throws InputTidakValidException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new InputTidakValidException("Nama barang tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public double getTarifHarian() {
        return tarifHarian;
    }

    public void setTarifHarian(double tarifHarian) throws InputTidakValidException {
        if (tarifHarian <= 0) {
            throw new InputTidakValidException("Tarif harian harus lebih dari 0.");
        }
        this.tarifHarian = tarifHarian;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    public StatusBarang getStatus() {
        return tersedia ? StatusBarang.TERSEDIA : StatusBarang.DISEWA;
    }
}
