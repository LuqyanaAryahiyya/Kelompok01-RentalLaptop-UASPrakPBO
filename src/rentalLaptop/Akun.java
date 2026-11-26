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

/**
 * Representasi akun pengguna dengan peran {@code Penyewa}.
 * Memiliki {@link KeranjangSewa} dan riwayat transaksi sewa yang telah diterima admin.
 */
class Penyewa extends Akun {

    private final KeranjangSewa keranjang;
    private final ArrayList<Transaksi> riwayat;

    /** Membuat instance {@code Penyewa} baru dengan keranjang dan riwayat kosong. */
    public Penyewa(String id, String username, String password, String nama) {
        super(id, username, password, nama);
        this.keranjang = new KeranjangSewa();
        this.riwayat = new ArrayList<>();
    }

    /** Melakukan login khusus peran Penyewa dengan validasi kredensial dan pesan sambutan. */
    @Override
    public void login(String username, String password) throws LoginGagalException {
        super.login(username, password);
        System.out.println("Login berhasil sebagai Penyewa: " + getNama() + ".");
    }

    @Override
    public String getPeran() {
        return "Penyewa";
    }

    /** Mengembalikan daftar barang yang sedang tersedia untuk disewa. */
    public List<Barang> lihatBarang(DaftarBarang daftarBarang) {
        if (daftarBarang == null) {
            return Collections.emptyList();
        }
        return daftarBarang.getBarangTersedia();
    }
