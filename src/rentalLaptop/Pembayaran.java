package rentalLaptop;

import java.util.UUID;
// Interface untuk semua metode pembayaran.
public interface Pembayaran {

    // Menjalankan proses pembayaran.
    void prosesPembayaran(double jumlah) throws InputTidakValidException;

    // Mengambil nama metode pembayaran.
    String getNamaMetode();

    // Mengambil keterangan pembayaran untuk invoice.
    String getKeterangan();
}

// Implementasi pembayaran menggunakan transfer bank.
class TransferBank implements Pembayaran {

    private final String nomorRekening;

    // Membuat data transfer bank dan memvalidasi nomor rekening.
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

// Implementasi pembayaran menggunakan QRIS.
class QRIS implements Pembayaran {

    private final String kodeReferensi;

    // Membuat kode referensi QRIS secara otomatis.
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

// Implementasi pembayaran tunai di tempat.
class Tunai implements Pembayaran {

    // Membuat objek pembayaran tunai.
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
