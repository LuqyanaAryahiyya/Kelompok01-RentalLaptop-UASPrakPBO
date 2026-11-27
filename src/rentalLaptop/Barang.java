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

abstract class Laptop extends Barang {

    private String spesifikasi;

    protected Laptop(String id, String nama, double tarifHarian, String spesifikasi)
            throws InputTidakValidException {
        super(id, nama, tarifHarian);
        setSpesifikasi(spesifikasi);
    }

    public String getSpesifikasi() {
        return spesifikasi;
    }

    public void setSpesifikasi(String spesifikasi) throws InputTidakValidException {
        if (spesifikasi == null || spesifikasi.trim().isEmpty()) {
            throw new InputTidakValidException("Spesifikasi laptop tidak boleh kosong.");
        }
        this.spesifikasi = spesifikasi.trim();
    }

    public abstract String getKeteranganBiaya();
}

class LaptopStandard extends Laptop {

    public static final double TARIF_DEFAULT = 50_000.0;

    public LaptopStandard(String id, String nama, double tarifHarian, String spesifikasi)
            throws InputTidakValidException {
        super(id, nama, tarifHarian, spesifikasi);
    }

    public LaptopStandard(String id, String nama, String spesifikasi)
            throws InputTidakValidException {
        this(id, nama, TARIF_DEFAULT, spesifikasi);
    }

    @Override
    public double hitungBiaya(int hari) {
        return getTarifHarian() * hari;
    }

    @Override
    public String getKategori() {
        return "Standard";
    }

    @Override
    public String getKeteranganBiaya() {
        return "Tanpa biaya tambahan";
    }
}


