package com.rentallaptop;

import java.util.UUID;
/**
 * Interface yang mendefinisikan kontrak metode pembayaran simulasi
 * pada sistem Rental Laptop.
 */
public interface Pembayaran {

    /** Menjalankan simulasi proses pembayaran sebesar jumlah tagihan yang ditentukan. */
    void prosesPembayaran(double jumlah) throws InputTidakValidException;

    /** Mengembalikan nama metode pembayaran. */
    String getNamaMetode();

    /** Mengembalikan keterangan atau referensi ringkas metode pembayaran untuk dicetak pada invoice. */
    String getKeterangan();
}

/**
 * Implementasi {@link Pembayaran} untuk simulasi pembayaran melalui Transfer Bank.
 * Membutuhkan nomor rekening pengirim/tujuan yang valid.
 */
class TransferBank implements Pembayaran {

    private final String nomorRekening;

    /** Membuat instance pembayaran Transfer Bank dengan nomor rekening tertentu. */
    public TransferBank(String nomorRekening) throws InputTidakValidException {
        if (nomorRekening == null || nomorRekening.trim().isEmpty()) {
            throw new InputTidakValidException("Nomor rekening tidak boleh kosong.");
        }
        String bersih = nomorRekening.trim().replaceAll("[\\s-]", "");
        if (!bersih.matches("\\d{6,20}")) {
            throw new InputTidakValidException(
                    "Nomor rekening harus berupa 6 sampai 20 digit angka.");
        }
        this.nomorRekening = bersih;
    }

    public String getNomorRekening() {
        return nomorRekening;
    }

    @Override
    public void prosesPembayaran(double jumlah) throws InputTidakValidException {
        if (jumlah <= 0) {
            throw new InputTidakValidException("Jumlah pembayaran harus lebih dari 0.");
        }
        System.out.println("[Simulasi Transfer Bank]");
        System.out.println("Nomor Rekening : " + nomorRekening);
        System.out.println("Nominal        : " + FormatHelper.formatRupiah(jumlah));
        System.out.println("Status         : Konfirmasi transfer bank berhasil dicatat.");
    }

    @Override
    public String getNamaMetode() {
        return "Transfer Bank";
    }

    @Override
    public String getKeterangan() {
        return "Rek. " + nomorRekening;
    }
}

/**
 * Implementasi {@link Pembayaran} untuk simulasi pembayaran melalui QRIS.
 * Kode referensi QRIS dibuat secara otomatis.
 */
class QRIS implements Pembayaran {

    private final String kodeReferensi;

    /** Membuat instance pembayaran QRIS dengan kode referensi otomatis. */
    public QRIS() {
        String acak = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        this.kodeReferensi = "QRIS-" + acak;
    }

    public String getKodeReferensi() {
        return kodeReferensi;
    }

    @Override
    public void prosesPembayaran(double jumlah) throws InputTidakValidException {
        if (jumlah <= 0) {
            throw new InputTidakValidException("Jumlah pembayaran harus lebih dari 0.");
        }
        System.out.println("[Simulasi QRIS]");
        System.out.println("Kode Referensi : " + kodeReferensi);
        System.out.println("Nominal        : " + FormatHelper.formatRupiah(jumlah));
        System.out.println("Status         : Pembayaran QRIS berhasil dikonfirmasi.");
    }

    @Override
    public String getNamaMetode() {
        return "QRIS";
    }

    @Override
    public String getKeterangan() {
        return "Ref. " + kodeReferensi;
    }
}

/** Implementasi {@link Pembayaran} untuk simulasi pembayaran tunai di tempat. */
class Tunai implements Pembayaran {

    /** Membuat instance pembayaran Tunai. */
    public Tunai() {
    }

    @Override
    public void prosesPembayaran(double jumlah) throws InputTidakValidException {
        if (jumlah <= 0) {
            throw new InputTidakValidException("Jumlah pembayaran harus lebih dari 0.");
        }
        System.out.println("[Simulasi Pembayaran Tunai]");
        System.out.println("Nominal        : " + FormatHelper.formatRupiah(jumlah));
        System.out.println("Status         : Pembayaran tunai di tempat dikonfirmasi.");
    }

    @Override
    public String getNamaMetode() {
        return "Tunai";
    }

    @Override
    public String getKeterangan() {
        return "Bayar di tempat";
    }
}
