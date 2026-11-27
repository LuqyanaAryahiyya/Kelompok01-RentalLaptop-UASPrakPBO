package rentalLaptop;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// Class untuk mengelola transaksi penyewaan laptop.
public class Transaksi {

    // Batas minimum lama sewa.
    public static final int LAMA_SEWA_MIN = 1;

    // Batas maksimum lama sewa.
    public static final int LAMA_SEWA_MAX = 30;

    // Persentase denda keterlambatan per hari.
    public static final double PERSENTASE_DENDA_HARIAN = 1.50;

    private final String id;
    private final Penyewa penyewa;
    private final ArrayList<Barang> daftarBarang;
    private final int lamaSewa;
    private LocalDate tanggalSewa;
    private LocalDate tanggalJatuhTempo;
    private LocalDate tanggalKembali;
    private StatusTransaksi status;
    private final Pembayaran pembayaran;
    private Invoice invoice;

    // Membuat transaksi baru dan memeriksa data yang dimasukkan.
    public Transaksi(String id, Penyewa penyewa, List<Barang> daftarBarang,
            int lamaSewa, Pembayaran pembayaran) throws InputTidakValidException {
        if (id == null || id.trim().isEmpty()) {
            throw new InputTidakValidException("ID transaksi tidak boleh kosong.");
        }
        if (penyewa == null) {
            throw new InputTidakValidException("Data penyewa tidak boleh kosong.");
        }
        if (daftarBarang == null || daftarBarang.isEmpty()) {
            throw new InputTidakValidException(
                    "Daftar sewa (keranjang) masih kosong. Tambahkan laptop terlebih dahulu.");
        }
        if (lamaSewa < LAMA_SEWA_MIN || lamaSewa > LAMA_SEWA_MAX) {
            throw new InputTidakValidException(
                    "Lama sewa harus antara " + LAMA_SEWA_MIN + " sampai " + LAMA_SEWA_MAX + " hari.");
        }
        if (pembayaran == null) {
            throw new InputTidakValidException("Metode pembayaran harus dipilih.");
        }

        this.id = id.trim().toUpperCase();
        this.penyewa = penyewa;
        this.daftarBarang = new ArrayList<>(daftarBarang);
        this.lamaSewa = lamaSewa;
        this.pembayaran = pembayaran;
        this.status = StatusTransaksi.MENUNGGU;
    }

    // Menghitung total biaya sewa semua laptop.
    public double hitungTotal() {
        double total = 0.0;
        for (Barang barang : daftarBarang) {
            total += barang.hitungBiaya(lamaSewa);
        }
        return total;
    }

    public long getHariTerlambat() {
        if (tanggalJatuhTempo == null || tanggalKembali == null) {
            return 0L;
        }
        return Math.max(0L, ChronoUnit.DAYS.between(tanggalJatuhTempo, tanggalKembali));
    }

    // Menghitung total denda jika pengembalian terlambat.
    public double hitungDenda() {
        long hariTerlambat = getHariTerlambat();
        if (hariTerlambat <= 0) {
            return 0.0;
        }
        double totalDenda = 0.0;
        for (Barang barang : daftarBarang) {
            totalDenda += hariTerlambat * PERSENTASE_DENDA_HARIAN * barang.getTarifHarian();
        }
        return totalDenda;
    }

    // Menghitung total biaya sewa ditambah denda.
    public double hitungTotalAkhir() {
        return hitungTotal() + hitungDenda();
    }

    // Mengubah status transaksi.
    public void ubahStatus(StatusTransaksi statusBaru) throws InputTidakValidException {
        if (statusBaru == null) {
            throw new InputTidakValidException("Status transaksi tidak boleh kosong.");
        }
        this.status = statusBaru;
    }

    // Menerima transaksi dan menandai laptop sedang disewa.
    public void terima(LocalDate tanggalTerima)
            throws BarangTidakTersediaException, InputTidakValidException {
        if (this.status != StatusTransaksi.MENUNGGU) {
            throw new InputTidakValidException(
                    "Transaksi " + id + " tidak dapat diterima karena berstatus " + status + ".");
        }
        if (tanggalTerima == null) {
            throw new InputTidakValidException("Tanggal penerimaan tidak boleh kosong.");
        }

        for (Barang barang : daftarBarang) {
            if (!barang.isTersedia()) {
                throw new BarangTidakTersediaException(
                        "Penerimaan gagal: Laptop " + barang.getId() + " (" + barang.getNama()
                                + ") sedang DISEWA pada transaksi lain.");
            }
        }

        for (Barang barang : daftarBarang) {
            barang.setTersedia(false);
        }

        this.tanggalSewa = tanggalTerima;
        this.tanggalJatuhTempo = tanggalTerima.plusDays(lamaSewa);
        this.status = StatusTransaksi.DITERIMA;
        this.invoice = new Invoice(this, LocalDate.now());
    }

    // Menolak transaksi yang masih menunggu.
    public void tolak() throws InputTidakValidException {
        if (this.status != StatusTransaksi.MENUNGGU) {
            throw new InputTidakValidException(
                    "Transaksi " + id + " tidak dapat ditolak karena berstatus " + status + ".");
        }
        this.status = StatusTransaksi.DITOLAK;
    }

    // Mengonfirmasi pengembalian dan memperbarui status laptop.
    public void konfirmasiKembali(LocalDate tanggalKembali) throws InputTidakValidException {
        if (this.status != StatusTransaksi.DITERIMA) {
            throw new InputTidakValidException(
                    "Pengembalian hanya dapat dikonfirmasi untuk transaksi berstatus DITERIMA.");
        }
        if (tanggalKembali == null) {
            throw new InputTidakValidException("Tanggal kembali tidak boleh kosong.");
        }
        if (tanggalKembali.isBefore(this.tanggalSewa)) {
            throw new InputTidakValidException(
                    "Tanggal kembali (" + FormatHelper.formatTanggal(tanggalKembali)
                            + ") tidak boleh sebelum tanggal sewa ("
                            + FormatHelper.formatTanggal(this.tanggalSewa) + ").");
        }

        this.tanggalKembali = tanggalKembali;
        for (Barang barang : daftarBarang) {
            barang.setTersedia(true);
        }
        this.status = StatusTransaksi.SELESAI;

        if (this.invoice == null) {
            this.invoice = new Invoice(this, LocalDate.now());
        } else {
            this.invoice.perbaruiTanggalCetak(LocalDate.now());
        }
    }

    public String getId() {
        return id;
    }

    public Penyewa getPenyewa() {
        return penyewa;
    }

    public List<Barang> getDaftarBarang() {
        return Collections.unmodifiableList(daftarBarang);
    }

    public int getLamaSewa() {
        return lamaSewa;
    }

    public LocalDate getTanggalSewa() {
        return tanggalSewa;
    }

    public LocalDate getTanggalJatuhTempo() {
        return tanggalJatuhTempo;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    public StatusTransaksi getStatus() {
        return status;
    }

    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    public Invoice getInvoice() {
        return invoice;
    }
}

// Daftar status yang dapat dimiliki transaksi.
enum StatusTransaksi {
    // Status awal setelah transaksi dibuat.
    MENUNGGU,
    // Status ketika transaksi diterima admin.
    DITERIMA,
    // Status ketika transaksi ditolak admin.
    DITOLAK,
    // Status setelah laptop dikembalikan.
    SELESAI
}

// Class untuk membuat dan menampilkan invoice transaksi.
class Invoice {

    private final String nomorInvoice;
    private final Transaksi transaksi;
    private LocalDate tanggalCetak;

    // Membuat invoice berdasarkan transaksi.
    public Invoice(Transaksi transaksi, LocalDate tanggalCetak) {
        this.transaksi = transaksi;
        this.nomorInvoice = "INV-" + transaksi.getId();
        this.tanggalCetak = tanggalCetak == null ? LocalDate.now() : tanggalCetak;
    }

    // Memperbarui tanggal cetak invoice.
    public void perbaruiTanggalCetak(LocalDate tanggalCetak) {
        if (tanggalCetak != null) {
            this.tanggalCetak = tanggalCetak;
        }
    }

    public String getNomorInvoice() {
        return nomorInvoice;
    }

    public Transaksi getTransaksi() {
        return transaksi;
    }

    public LocalDate getTanggalCetak() {
        return tanggalCetak;
    }

    // Menyusun isi invoice dalam bentuk teks.
    public String formatInvoice() {
        StringBuilder sb = new StringBuilder();
        String garis = FormatHelper.garis(74);

        sb.append("\n").append(garis).append("\n");
        sb.append(String.format("%-38s %35s%n", "INVOICE RENTAL LAPTOP", nomorInvoice));
        sb.append(garis).append("\n");
        sb.append(String.format("%-20s : %s%n", "Nomor Invoice", nomorInvoice));
        sb.append(String.format("%-20s : %s%n", "ID Transaksi", transaksi.getId()));
        sb.append(String.format("%-20s : %s%n", "Tanggal Cetak",
                FormatHelper.formatTanggal(tanggalCetak)));
        sb.append(String.format("%-20s : %s (%s)%n", "Nama Penyewa",
                transaksi.getPenyewa().getNama(), transaksi.getPenyewa().getUsername()));
        sb.append(String.format("%-20s : %s%n", "Status Transaksi", transaksi.getStatus()));
        sb.append(String.format("%-20s : %s%n", "Tanggal Sewa",
                FormatHelper.formatTanggal(transaksi.getTanggalSewa())));
        sb.append(String.format("%-20s : %s%n", "Tanggal Jatuh Tempo",
                FormatHelper.formatTanggal(transaksi.getTanggalJatuhTempo())));

        if (transaksi.getTanggalKembali() != null) {
            sb.append(String.format("%-20s : %s%n", "Tanggal Kembali",
                    FormatHelper.formatTanggal(transaksi.getTanggalKembali())));
            sb.append(String.format("%-20s : %d hari%n", "Keterlambatan",
                    transaksi.getHariTerlambat()));
        }

        sb.append(String.format("%-20s : %s (%s)%n", "Metode Pembayaran",
                transaksi.getPembayaran().getNamaMetode(),
                transaksi.getPembayaran().getKeterangan()));

        sb.append(garis).append("\n");
        sb.append(String.format("%-9s %-24s %-12s %6s %17s%n",
                "ID", "Nama Laptop", "Kategori", "Hari", "Subtotal"));
        sb.append(garis).append("\n");

        for (Barang barang : transaksi.getDaftarBarang()) {
            double subtotal = barang.hitungBiaya(transaksi.getLamaSewa());
            sb.append(String.format("%-9s %-24s %-12s %6d %17s%n",
                    barang.getId(),
                    FormatHelper.potongTeks(barang.getNama(), 24),
                    barang.getKategori(),
                    transaksi.getLamaSewa(),
                    FormatHelper.formatRupiah(subtotal)));
        }

        sb.append(garis).append("\n");
        sb.append(String.format("%-55s %18s%n", "Total Biaya Sewa",
                FormatHelper.formatRupiah(transaksi.hitungTotal())));

        double denda = transaksi.hitungDenda();
        if (transaksi.getStatus() == StatusTransaksi.SELESAI || denda > 0) {
            sb.append(String.format("%-55s %18s%n",
                    "Denda Keterlambatan (" + transaksi.getHariTerlambat() + " hari)",
                    FormatHelper.formatRupiah(denda)));
        }
        sb.append(String.format("%-55s %18s%n", "TOTAL AKHIR",
                FormatHelper.formatRupiah(transaksi.hitungTotalAkhir())));
        sb.append(garis).append("\n");

        return sb.toString();
    }

    // Menampilkan invoice ke terminal.
    public void cetakInvoice() {
        System.out.println(formatInvoice());
    }
}
