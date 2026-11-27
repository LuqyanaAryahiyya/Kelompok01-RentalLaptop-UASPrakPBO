package com.rentallaptop;

import java.util.ArrayList;
import java.util.List;
// Class untuk menyimpan dan mengelola daftar transaksi.
public class DaftarTransaksi {

    private final ArrayList<Transaksi> daftarTransaksi;
    private int nomorUrut;

    // Membuat daftar transaksi kosong.
    public DaftarTransaksi() {
        this.daftarTransaksi = new ArrayList<>();
        this.nomorUrut = 0;
    }

    // Membuat ID transaksi secara berurutan.
    public String generateId() {
        nomorUrut++;
        return String.format("TRX-%03d", nomorUrut);
    }

    // Menambahkan transaksi ke dalam daftar.
    public void tambahTransaksi(Transaksi transaksi) throws InputTidakValidException {
        if (transaksi == null) {
            throw new InputTidakValidException("Transaksi tidak boleh kosong.");
        }
        daftarTransaksi.add(transaksi);
    }

    // Mencari transaksi berdasarkan ID.
    public Transaksi cariById(String id) throws InputTidakValidException {
        if (id == null || id.trim().isEmpty()) {
            throw new InputTidakValidException("ID transaksi tidak boleh kosong.");
        }
        String kunci = id.trim().toUpperCase();
        for (Transaksi trx : daftarTransaksi) {
            if (trx.getId().equalsIgnoreCase(kunci)) {
                return trx;
            }
        }
        throw new InputTidakValidException("Transaksi dengan ID '" + kunci + "' tidak ditemukan.");
    }

    public List<Transaksi> getSemuaTransaksi() {
        return new ArrayList<>(daftarTransaksi);
    }

    public List<Transaksi> getTransaksiByStatus(StatusTransaksi status) {
        List<Transaksi> hasil = new ArrayList<>();
        for (Transaksi trx : daftarTransaksi) {
            if (trx.getStatus() == status) {
                hasil.add(trx);
            }
        }
        return hasil;
    }

    public List<Transaksi> getTransaksiByPenyewa(Penyewa penyewa) {
        List<Transaksi> hasil = new ArrayList<>();
        if (penyewa == null) {
            return hasil;
        }
        for (Transaksi trx : daftarTransaksi) {
            if (trx.getPenyewa().getId().equals(penyewa.getId())) {
                hasil.add(trx);
            }
        }
        return hasil;
    }

    public List<Transaksi> getPengajuanBelumDiterima(Penyewa penyewa) {
        List<Transaksi> hasil = new ArrayList<>();
        for (Transaksi trx : getTransaksiByPenyewa(penyewa)) {
            if (trx.getStatus() == StatusTransaksi.MENUNGGU
                    || trx.getStatus() == StatusTransaksi.DITOLAK) {
                hasil.add(trx);
            }
        }
        return hasil;
    }
}
