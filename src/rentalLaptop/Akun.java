package com.rentallaptop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Akun {

    private String id;
    private String username;
    private String password;
    private String nama;

    protected Akun(String id, String username, String password, String nama) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nama = nama;
    }

    public void login(String username, String password) throws LoginGagalException {
        if (username == null || password == null
                || !this.username.equals(username)
                || !this.password.equals(password)) {
            throw new LoginGagalException("Username atau password salah.");
        }
    }

    public abstract String getPeran();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean cocokPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}

class Admin extends Akun {

    public Admin(String id, String username, String password, String nama) {
        super(id, username, password, nama);
    }

    @Override
    public void login(String username, String password) throws LoginGagalException {
        super.login(username, password);
        System.out.println("Login berhasil sebagai Admin: " + getNama() + ".");
    }

    @Override
    public String getPeran() {
        return "Admin";
    }

    public void kelolaBarang(DaftarBarang daftarBarang, Barang barangBaru)
            throws InputTidakValidException {
        if (daftarBarang == null) {
            throw new InputTidakValidException("Daftar barang tidak tersedia.");
        }
        daftarBarang.tambahBarang(barangBaru);
    }

    public void terimaTransaksi(Transaksi transaksi, DaftarBarang daftarBarang)
            throws BarangTidakTersediaException, BarangTidakDitemukanException,
                   InputTidakValidException {
        if (transaksi == null) {
            throw new InputTidakValidException("Transaksi tidak boleh kosong.");
        }
        if (daftarBarang == null) {
            throw new InputTidakValidException("Daftar barang tidak tersedia.");
        }
        for (Barang barang : transaksi.getDaftarBarang()) {
            daftarBarang.cariById(barang.getId());
        }
        transaksi.terima(LocalDate.now());
        transaksi.getPenyewa().tambahRiwayat(transaksi);
    }

    public void tolakTransaksi(Transaksi transaksi) throws InputTidakValidException {
        if (transaksi == null) {
            throw new InputTidakValidException("Transaksi tidak boleh kosong.");
        }
        transaksi.tolak();
    }

    public void konfirmasiKembali(Transaksi transaksi, LocalDate tanggalKembali)
            throws InputTidakValidException {
        if (transaksi == null) {
            throw new InputTidakValidException("Transaksi tidak boleh kosong.");
        }
        transaksi.konfirmasiKembali(tanggalKembali);
    }
}

class Penyewa extends Akun {

    private final KeranjangSewa keranjang;
    private final ArrayList<Transaksi> riwayat;

    public Penyewa(String id, String username, String password, String nama) {
        super(id, username, password, nama);
        this.keranjang = new KeranjangSewa();
        this.riwayat = new ArrayList<>();
    }

    @Override
    public void login(String username, String password) throws LoginGagalException {
        super.login(username, password);
        System.out.println("Login berhasil sebagai Penyewa: " + getNama() + ".");
    }

    @Override
    public String getPeran() {
        return "Penyewa";
    }

    public List<Barang> lihatBarang(DaftarBarang daftarBarang) {
        if (daftarBarang == null) {
            return Collections.emptyList();
        }
        return daftarBarang.getBarangTersedia();
    }

      public Transaksi buatTransaksi(int lamaSewa, Pembayaran pembayaran,
            DaftarTransaksi daftarTransaksi, DaftarBarang daftarBarang)
            throws InputTidakValidException, BarangTidakTersediaException,
                   BarangTidakDitemukanException {
        if (keranjang.isEmpty()) {
            throw new InputTidakValidException(
                    "Daftar sewa (keranjang) masih kosong. Tambahkan laptop terlebih dahulu.");
        }
        if (lamaSewa < Transaksi.LAMA_SEWA_MIN || lamaSewa > Transaksi.LAMA_SEWA_MAX) {
            throw new InputTidakValidException(
                    "Lama sewa harus antara " + Transaksi.LAMA_SEWA_MIN + " sampai "
                            + Transaksi.LAMA_SEWA_MAX + " hari.");
        }
        if (pembayaran == null) {
            throw new InputTidakValidException("Metode pembayaran belum dipilih.");
        }
        if (daftarTransaksi == null || daftarBarang == null) {
            throw new InputTidakValidException("Layanan transaksi atau katalog tidak tersedia.");
        }

        for (Barang item : keranjang.getDaftarBarang()) {
            Barang aktual = daftarBarang.cariById(item.getId());
            if (!aktual.isTersedia()) {
                throw new BarangTidakTersediaException(
                        "Laptop " + aktual.getId() + " (" + aktual.getNama()
                                + ") sudah berstatus DISEWA. Hapus dari daftar sewa terlebih dahulu.");
            }
        }

        double totalBayar = keranjang.hitungEstimasiTotal(lamaSewa);
        pembayaran.prosesPembayaran(totalBayar);

        String idTransaksi = daftarTransaksi.generateId();
        Transaksi transaksi = new Transaksi(
                idTransaksi, this, keranjang.getDaftarBarang(), lamaSewa, pembayaran);
        daftarTransaksi.tambahTransaksi(transaksi);
        keranjang.kosongkan();
        return transaksi;
    }
    
    public void tambahRiwayat(Transaksi transaksi) {
        if (transaksi != null && !riwayat.contains(transaksi)) {
            riwayat.add(transaksi);
        }
    }
    
    public List<Transaksi> lihatRiwayat() {
        return Collections.unmodifiableList(riwayat);
    }

    public KeranjangSewa getKeranjang() {
        return keranjang;
    }
}
