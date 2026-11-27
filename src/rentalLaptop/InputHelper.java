package com.rentallaptop;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public final class InputHelper{

    private static final DateTimeFormatter STRICT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private InputHelper(){
    }

    //membaca baris teks dari setelah mencetak dan melakukan flush prompt
    public static String bacaString(Scanner scanner, String prompt){
        String hasil = bacaBarisAtauNull(scanner, prompt);
        return hasil == null ? "" : hasil;
    }

    //mencetak prompt lalu membaca satu baris input
    public static String bacaBarisAtauNull(Scanner scanner, String prompt){
        System.out.print(prompt);
        System.out.flush();
        if (!scanner.hasNextLine()){
            return null;
        }
        return scanner.nextLine().trim();
    }

    //mengonversi teks menjadi bilangan bulat dengan pesan error ramah jika gagal
    public static int parseInt(String teks) throws InputTidakValidException{
        try{
            return Integer.parseInt(teks);
        } 
        catch (NumberFormatException e){
            throw new InputTidakValidException("Input harus berupa angka bulat yang valid.");
        }
    }

    public static String bacaStringWajib(Scanner scanner, String prompt, String label)
            throws InputTidakValidException{
        String nilai = bacaString(scanner, prompt);
        if (nilai.isEmpty()){
            throw new InputTidakValidException(label + " tidak boleh kosong.");
        }
        return nilai;
    }

    public static int bacaInt(Scanner scanner, String prompt) throws InputTidakValidException{
        String teks = bacaString(scanner, prompt);
        try{
            return Integer.parseInt(teks);
        } 
        catch (NumberFormatException e){
            throw new InputTidakValidException("Input harus berupa angka bulat yang valid.");
        }
    }

    public static double bacaDouble(Scanner scanner, String prompt)
            throws InputTidakValidException{
        String teks = bacaString(scanner, prompt);
        try{
            return Double.parseDouble(teks);
        } 
        catch (NumberFormatException e){
            throw new InputTidakValidException("Input harus berupa angka yang valid.");
        }
    }

    public static LocalDate bacaTanggal(Scanner scanner, String prompt)
            throws InputTidakValidException{
        String teks = bacaString(scanner, prompt);
        try{
            return LocalDate.parse(teks, STRICT_DATE_FORMAT);
        } 
        catch (DateTimeParseException e){
            throw new InputTidakValidException(
                    "Format tanggal tidak valid. Gunakan format yyyy-MM-dd (contoh: 2026-11-28).");
        }
    }

    public static boolean bacaKonfirmasi(Scanner scanner, String prompt)
            throws InputTidakValidException{
        String teks = bacaString(scanner, prompt).toLowerCase();
        if (teks.equals("y") || teks.equals("ya")){
            return true;
        }
        if (teks.equals("n") || teks.equals("tidak") || teks.equals("t")){
            return false;
        }
        throw new InputTidakValidException("Masukkan 'y' untuk ya atau 'n' untuk tidak.");
    }
}

final class FormatHelper{

    private static final Locale LOCALE_ID = new Locale("id", "ID");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private FormatHelper(){
    }

    public static String formatRupiah(double jumlah){
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_ID);
        nf.setMaximumFractionDigits(0);
        return "Rp" + nf.format(Math.round(jumlah));
    }

    public static String formatTanggal(LocalDate tanggal){
        return tanggal == null ? "-" : tanggal.format(DATE_FORMATTER);
    }

    public static String potongTeks(String teks, int maxPanjang){
        if (teks == null){
            return "";
        }
        if (teks.length() <= maxPanjang){
            return teks;
        }
        if (maxPanjang <= 3){
            return teks.substring(0, maxPanjang);
        }
        return teks.substring(0, maxPanjang - 3) + "...";
    }

    public static String garis(int panjang){
        return "-".repeat(Math.max(0, panjang));
    }
}

final class TabelHelper{

    private TabelHelper(){
    }

    public static void cetakTabelBarang(String judul, List<Barang> daftarBarang){
        System.out.println("\n=== " + judul + " ===");
        if (daftarBarang == null || daftarBarang.isEmpty()){
            System.out.println("Tidak ada data laptop.");
            return;
        }

        String garis = FormatHelper.garis(112);
        System.out.println(garis);
        System.out.printf("%-9s %-24s %-12s %-14s %-10s %-38s%n",
                "ID", "Nama Laptop", "Kategori", "Tarif/Hari", "Status", "Spesifikasi");
        System.out.println(garis);

        for (Barang barang : daftarBarang){
            String spek = (barang instanceof Laptop)
                    ? ((Laptop) barang).getSpesifikasi()
                    : "-";
            System.out.printf("%-9s %-24s %-12s %-14s %-10s %-38s%n",
                    barang.getId(),
                    FormatHelper.potongTeks(barang.getNama(), 24),
                    barang.getKategori(),
                    FormatHelper.formatRupiah(barang.getTarifHarian()),
                    barang.getStatus(),
                    FormatHelper.potongTeks(spek, 38));
        }
        System.out.println(garis);
    }

    public static void cetakTabelTransaksi(String judul, List<Transaksi> daftarTransaksi){
        System.out.println("\n=== " + judul + " ===");
        if (daftarTransaksi == null || daftarTransaksi.isEmpty()){
            System.out.println("Tidak ada data transaksi.");
            return;
        }

        String garis = FormatHelper.garis(108);
        System.out.println(garis);
        System.out.printf("%-9s %-16s %-6s %-11s %-12s %-12s %-14s %-15s%n",
                "ID", "Penyewa", "Hari", "Status", "Tgl Sewa", "Jatuh Tempo", "Pembayaran",
                "Total Akhir");
        System.out.println(garis);

        for (Transaksi trx : daftarTransaksi){
            System.out.printf("%-9s %-16s %-6d %-11s %-12s %-12s %-14s %-15s%n",
                    trx.getId(),
                    FormatHelper.potongTeks(trx.getPenyewa().getNama(), 16),
                    trx.getLamaSewa(),
                    trx.getStatus(),
                    FormatHelper.formatTanggal(trx.getTanggalSewa()),
                    FormatHelper.formatTanggal(trx.getTanggalJatuhTempo()),
                    FormatHelper.potongTeks(trx.getPembayaran().getNamaMetode(), 14),
                    FormatHelper.formatRupiah(trx.hitungTotalAkhir()));
        }
        System.out.println(garis);
    }

    public static void cetakDetailTransaksi(Transaksi trx){
        System.out.println("\n--- Detail Transaksi " + trx.getId() + " ---");
        System.out.println("Penyewa     : " + trx.getPenyewa().getNama()
                + " (" + trx.getPenyewa().getUsername() + ")");
        System.out.println("Lama Sewa   : " + trx.getLamaSewa() + " hari");
        System.out.println("Pembayaran  : " + trx.getPembayaran().getNamaMetode()
                + " (" + trx.getPembayaran().getKeterangan() + ")");
        System.out.println("Status      : " + trx.getStatus());

        String garis = FormatHelper.garis(72);
        System.out.println(garis);
        System.out.printf("%-9s %-24s %-12s %-10s %-14s%n",
                "ID", "Nama Laptop", "Kategori", "Status", "Subtotal");
        System.out.println(garis);
        for (Barang barang : trx.getDaftarBarang()){
            System.out.printf("%-9s %-24s %-12s %-10s %-14s%n",
                    barang.getId(),
                    FormatHelper.potongTeks(barang.getNama(), 24),
                    barang.getKategori(),
                    barang.getStatus(),
                    FormatHelper.formatRupiah(barang.hitungBiaya(trx.getLamaSewa())));
        }
        System.out.println(garis);
        System.out.println("Total Biaya Sewa : " + FormatHelper.formatRupiah(trx.hitungTotal()));
    }
}
