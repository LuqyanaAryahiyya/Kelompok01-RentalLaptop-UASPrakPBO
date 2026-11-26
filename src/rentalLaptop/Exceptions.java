package com.rentallaptop;
/**
 * Exception yang dilempar ketika proses autentikasi (login) gagal,
 * misalnya karena username tidak ditemukan atau password salah.
 */
class LoginGagalException extends Exception {

    /** Membuat instance {@code LoginGagalException} dengan pesan kesalahan. */
    public LoginGagalException(String message) {
        super(message);
    }

    /** Membuat instance {@code LoginGagalException} dengan pesan kesalahan dan penyebab awal. */
    public LoginGagalException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception yang dilempar ketika input pengguna atau parameter operasi tidak valid,
 * seperti lama sewa di luar rentang 1-30 hari, pilihan menu salah, format angka/tanggal
 * tidak valid, tarif kurang dari atau sama dengan nol, ID duplikat, atau keranjang kosong.
 */
class InputTidakValidException extends Exception {

    /** Membuat instance {@code InputTidakValidException} dengan pesan kesalahan. */
    public InputTidakValidException(String message) {
        super(message);
    }

    /** Membuat instance {@code InputTidakValidException} dengan pesan kesalahan dan penyebab awal. */
    public InputTidakValidException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception yang dilempar ketika barang (laptop) berstatus {@code DISEWA}
 * namun dipilih untuk disewa, diterima pada transaksi lain, atau akan dihapus.
 */
class BarangTidakTersediaException extends Exception {

    /** Membuat instance {@code BarangTidakTersediaException} dengan pesan kesalahan. */
    public BarangTidakTersediaException(String message) {
        super(message);
    }

    /** Membuat instance {@code BarangTidakTersediaException} dengan pesan kesalahan dan penyebab awal. */
    public BarangTidakTersediaException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Exception yang dilempar ketika barang (laptop) dengan ID tertentu
 * tidak ditemukan di daftar barang atau keranjang sewa.
 */
class BarangTidakDitemukanException extends Exception {

    /** Membuat instance {@code BarangTidakDitemukanException} dengan pesan kesalahan. */
    public BarangTidakDitemukanException(String message) {
        super(message);
    }

    /** Membuat instance {@code BarangTidakDitemukanException} dengan pesan kesalahan dan penyebab awal. */
    public BarangTidakDitemukanException(String message, Throwable cause) {
        super(message, cause);
    }
}
