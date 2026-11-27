package rentalLaptop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final int MAKS_PERCOBAAN_LOGIN = 3;

    private final List<Akun> daftarAkun;
    private final DaftarBarang daftarBarang;
    private final DaftarTransaksi daftarTransaksi;
    private final Scanner scanner;
    private Akun akunAktif;

    public Main() {
        this.daftarAkun = new ArrayList<>();
        this.daftarBarang = new DaftarBarang();
        this.daftarTransaksi = new DaftarTransaksi();
        this.scanner = new Scanner(System.in);
        inisialisasiAkun();
    }

    private void inisialisasiAkun() {
        daftarAkun.add(new Admin("ADM-001", "admin", "admin123", "Administrator"));
        daftarAkun.add(new Penyewa("PNY-001", "penyewa1", "pass123", "Penyewa Satu"));
        daftarAkun.add(new Penyewa("PNY-002", "penyewa2", "pass456", "Penyewa Dua"));
    }

    public Akun getAkunAktif() {
        return akunAktif;
    }

    public void jalankan() {
        boolean berjalan = true;
        while (berjalan) {
            int pilihanAwal = tampilkanMenuAwal();
            if (pilihanAwal == 3) {
                break;
            }

            Class<? extends Akun> peranTarget =
                    (pilihanAwal == 1) ? Admin.class : Penyewa.class;
            boolean berhasilLogin = login(peranTarget);
            if (!berhasilLogin) {
                break;
            }

            int hasilMenu;
            if (akunAktif instanceof Admin) {
                MenuAdmin menuAdmin = new MenuAdmin(
                        (Admin) akunAktif, daftarBarang, daftarTransaksi, scanner);
                hasilMenu = menuAdmin.tampilkanMenu();
            } else if (akunAktif instanceof Penyewa) {
                MenuPenyewa menuPenyewa = new MenuPenyewa(
                        (Penyewa) akunAktif, daftarBarang, daftarTransaksi, scanner);
                hasilMenu = menuPenyewa.tampilkanMenu();
            } else {
                hasilMenu = MenuAdmin.AKSI_KELUAR;
            }

            akunAktif = null;
            if (hasilMenu == MenuAdmin.AKSI_KELUAR || hasilMenu == MenuPenyewa.AKSI_KELUAR) {
                berjalan = false;
            }
        }
        System.out.println("Terima kasih telah menggunakan aplikasi Rental Laptop.");
    }

    private int tampilkanMenuAwal() {
        while (true) {
            System.out.println("\n=== RENTAL LAPTOP ===");
            System.out.println("1. Menu Admin");
            System.out.println("2. Menu Penyewa");
            System.out.println("3. Keluar");

            try {
                String input = InputHelper.bacaBarisAtauNull(scanner, "Pilih menu : ");
                if (input == null) {
                    return 3;
                }
                int pilihan = InputHelper.parseInt(input);
                if (pilihan >= 1 && pilihan <= 3) {
                    return pilihan;
                }
                throw new InputTidakValidException("Pilihan menu tidak valid. Pilih angka 1 sampai 3.");
            } catch (InputTidakValidException e) {
                System.out.println("[Kesalahan] " + e.getMessage());
            }
        }
    }

    public boolean login() {
        return login(Akun.class);
    }

    public boolean login(Class<? extends Akun> peranTarget) {
        String judulLogin = "LOGIN";
        if (peranTarget == Admin.class) {
            judulLogin = "LOGIN ADMIN";
        } else if (peranTarget == Penyewa.class) {
            judulLogin = "LOGIN PENYEWA";
        }

        int percobaan = 0;
        while (percobaan < MAKS_PERCOBAAN_LOGIN) {
            System.out.println("\n=== " + judulLogin + " ===");
            String username = InputHelper.bacaBarisAtauNull(scanner, "Username : ");
            if (username == null) {
                return false;
            }
            String password = InputHelper.bacaBarisAtauNull(scanner, "Password : ");
            if (password == null) {
                return false;
            }

            try {
                Akun cocok = cariDanAutentikasi(username, password, peranTarget);
                this.akunAktif = cocok;
                return true;
            } catch (LoginGagalException e) {
                percobaan++;
                int sisa = MAKS_PERCOBAAN_LOGIN - percobaan;
                System.out.println("[Login Gagal] " + e.getMessage());
                if (sisa > 0) {
                    System.out.println("Sisa percobaan login: " + sisa + " kali.");
                } else {
                    System.out.println(
                            "PERINGATAN: Anda telah gagal login 3 kali. Program dihentikan.");
                }
            }
        }
        return false;
    }

    private Akun cariDanAutentikasi(
            String username, String password, Class<? extends Akun> peranTarget)
            throws LoginGagalException {
        for (Akun akun : daftarAkun) {
            if (akun.getUsername().equals(username)) {
                if (peranTarget != null && !peranTarget.isInstance(akun)) {
                    throw new LoginGagalException(
                            "Akun '" + username + "' bukan merupakan akun "
                                    + (peranTarget == Admin.class ? "Admin." : "Penyewa."));
                }
                akun.login(username, password);
                return akun;
            }
        }
        throw new LoginGagalException("Username atau password salah.");
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.jalankan();
    }
}

class MenuAdmin {

    public static final int AKSI_LANJUT = 0;

    public static final int AKSI_LOGOUT = 1;

    public static final int AKSI_KELUAR = 2;

    private final Admin admin;
    private final DaftarBarang daftarBarang;
    private final DaftarTransaksi daftarTransaksi;
    private final Scanner scanner;

    public MenuAdmin(Admin admin, DaftarBarang daftarBarang,
            DaftarTransaksi daftarTransaksi, Scanner scanner) {
        this.admin = admin;
        this.daftarBarang = daftarBarang;
        this.daftarTransaksi = daftarTransaksi;
        this.scanner = scanner;
    }

    public int tampilkanMenu() {
        while (true) {
            System.out.println("\n=== MENU ADMIN (" + admin.getNama() + ") ===");
            System.out.println("1. Lihat Daftar Laptop");
            System.out.println("2. Tambah Laptop");
            System.out.println("3. Ubah Laptop");
            System.out.println("4. Hapus Laptop");
            System.out.println("5. Lihat Transaksi");
            System.out.println("6. Terima / Tolak Transaksi");
            System.out.println("7. Konfirmasi Pengembalian");
            System.out.println("8. Keluar Program");
            System.out.println("0. Logout");

            try {
                String inputMenu = InputHelper.bacaBarisAtauNull(scanner, "Pilih menu : ");
                if (inputMenu == null) {
                    return AKSI_KELUAR;
                }
                int pilihan = InputHelper.parseInt(inputMenu);
                switch (pilihan) {
                    case 1:
                        lihatDaftarLaptop();
                        break;
                    case 2:
                        tambahLaptop();
                        break;
                    case 3:
                        ubahLaptop();
                        break;
                    case 4:
                        hapusLaptop();
                        break;
                    case 5:
                        lihatTransaksi();
                        break;
                    case 6:
                        prosesPersetujuanTransaksi();
                        break;
                    case 7:
                        konfirmasiPengembalian();
                        break;
                    case 8:
                        return AKSI_KELUAR;
                    case 0:
                        System.out.println("Logout berhasil.");
                        return AKSI_LOGOUT;
                    default:
                        throw new InputTidakValidException(
                                "Pilihan menu tidak valid. Pilih angka 0 sampai 8.");
                }
            } catch (InputTidakValidException
                    | BarangTidakDitemukanException
                    | BarangTidakTersediaException e) {
                System.out.println("[Kesalahan] " + e.getMessage());
            }
        }
    }

    private void lihatDaftarLaptop() {
        TabelHelper.cetakTabelBarang("DAFTAR SELURUH LAPTOP", daftarBarang.getSemuaBarang());
    }

    private void tambahLaptop() throws InputTidakValidException {
        System.out.println("\n--- Tambah Laptop Baru ---");
        System.out.println("Pilih Kategori:");
        System.out.println("1. Standard    (Tarif default Rp50.000/hari)");
        System.out.println("2. Gaming      (Tarif default Rp100.000/hari + Perawatan Rp15.000/hari)");
        System.out.println("3. Workstation (Tarif default Rp150.000/hari + Asuransi 10%)");

        int kategori = InputHelper.bacaInt(scanner, "Kategori (1-3) : ");
        String prefix;
        double tarifDefault;
        switch (kategori) {
            case 1:
                prefix = "STD";
                tarifDefault = LaptopStandard.TARIF_DEFAULT;
                break;
            case 2:
                prefix = "GMG";
                tarifDefault = LaptopGaming.TARIF_DEFAULT;
                break;
            case 3:
                prefix = "WRK";
                tarifDefault = LaptopWorkstation.TARIF_DEFAULT;
                break;
            default:
                throw new InputTidakValidException("Kategori tidak valid. Pilih 1, 2, atau 3.");
        }

        String idOtomatis = daftarBarang.generateId(prefix);
        String inputId = InputHelper.bacaString(scanner,
                "ID Laptop (kosongkan untuk otomatis [" + idOtomatis + "]) : ");
        String idFinal = inputId.isEmpty() ? idOtomatis : inputId.toUpperCase();

        String nama = InputHelper.bacaStringWajib(scanner, "Nama/Merek Laptop : ", "Nama laptop");

        String inputTarif = InputHelper.bacaString(scanner,
                "Tarif Harian (kosongkan untuk default "
                        + FormatHelper.formatRupiah(tarifDefault) + ") : ");
        double tarifFinal = tarifDefault;
        if (!inputTarif.isEmpty()) {
            try {
                tarifFinal = Double.parseDouble(inputTarif);
            } catch (NumberFormatException e) {
                throw new InputTidakValidException("Tarif harian harus berupa angka yang valid.");
            }
            if (tarifFinal <= 0) {
                throw new InputTidakValidException("Tarif harian harus lebih dari 0.");
            }
        }

        String spesifikasi = InputHelper.bacaStringWajib(
                scanner, "Spesifikasi Ringkas : ", "Spesifikasi");

        Laptop laptopBaru;
        if (kategori == 1) {
            laptopBaru = new LaptopStandard(idFinal, nama, tarifFinal, spesifikasi);
        } else if (kategori == 2) {
            laptopBaru = new LaptopGaming(idFinal, nama, tarifFinal, spesifikasi);
        } else {
            laptopBaru = new LaptopWorkstation(idFinal, nama, tarifFinal, spesifikasi);
        }

        admin.kelolaBarang(daftarBarang, laptopBaru);
        System.out.println("Laptop berhasil ditambahkan: " + laptopBaru.getId()
                + " - " + laptopBaru.getNama() + " (" + laptopBaru.getKategori() + ").");
    }

    private void ubahLaptop()
            throws BarangTidakDitemukanException, InputTidakValidException {
        lihatDaftarLaptop();
        String id = InputHelper.bacaStringWajib(scanner, "Masukkan ID laptop yang akan diubah : ",
                "ID laptop");
        Barang barang = daftarBarang.cariById(id);

        System.out.println("Data saat ini -> Nama: " + barang.getNama()
                + " | Tarif: " + FormatHelper.formatRupiah(barang.getTarifHarian())
                + (barang instanceof Laptop
                        ? " | Spek: " + ((Laptop) barang).getSpesifikasi()
                        : ""));
        System.out.println("(Kosongkan input jika tidak ingin mengubah bagian tersebut)");

        String namaBaru = InputHelper.bacaString(scanner, "Nama baru : ");
        String inputTarif = InputHelper.bacaString(scanner, "Tarif harian baru : ");
        Double tarifBaru = null;
        if (!inputTarif.isEmpty()) {
            try {
                tarifBaru = Double.parseDouble(inputTarif);
            } catch (NumberFormatException e) {
                throw new InputTidakValidException("Tarif harian harus berupa angka yang valid.");
            }
        }
        String spekBaru = InputHelper.bacaString(scanner, "Spesifikasi baru : ");

        daftarBarang.ubahBarang(id, namaBaru, tarifBaru, spekBaru);
        System.out.println("Data laptop " + barang.getId() + " berhasil diperbarui.");
    }

    private void hapusLaptop()
            throws BarangTidakDitemukanException, BarangTidakTersediaException,
                   InputTidakValidException {
        lihatDaftarLaptop();
        String id = InputHelper.bacaStringWajib(scanner, "Masukkan ID laptop yang akan dihapus : ",
                "ID laptop");
        Barang target = daftarBarang.cariById(id);

        if (!target.isTersedia()) {
            throw new BarangTidakTersediaException(
                    "Laptop " + target.getId() + " (" + target.getNama()
                            + ") sedang DISEWA dan tidak boleh dihapus.");
        }

        boolean ya = InputHelper.bacaKonfirmasi(scanner,
                "Yakin menghapus laptop " + target.getId() + " - " + target.getNama() + "? (y/n) : ");
        if (!ya) {
            System.out.println("Penghapusan dibatalkan.");
            return;
        }

        Barang terhapus = daftarBarang.hapusBarang(id);
        System.out.println("Laptop " + terhapus.getId() + " (" + terhapus.getNama()
                + ") berhasil dihapus.");
    }

    private void lihatTransaksi() throws InputTidakValidException {
        System.out.println("\n--- Lihat Transaksi ---");
        System.out.println("1. Semua Transaksi");
        System.out.println("2. Transaksi Menunggu Persetujuan (MENUNGGU)");
        int pilihan = InputHelper.bacaInt(scanner, "Pilih tampilan (1-2) : ");

        List<Transaksi> daftar;
        if (pilihan == 1) {
            daftar = daftarTransaksi.getSemuaTransaksi();
            TabelHelper.cetakTabelTransaksi("SEMUA TRANSAKSI", daftar);
        } else if (pilihan == 2) {
            daftar = daftarTransaksi.getTransaksiByStatus(StatusTransaksi.MENUNGGU);
            TabelHelper.cetakTabelTransaksi("TRANSAKSI MENUNGGU PERSETUJUAN", daftar);
        } else {
            throw new InputTidakValidException("Pilihan tampilan tidak valid. Pilih 1 atau 2.");
        }

        if (!daftar.isEmpty()) {
            String id = InputHelper.bacaString(scanner,
                    "Masukkan ID transaksi untuk melihat detail (Enter untuk lewati) : ");
            if (!id.isEmpty()) {
                Transaksi trx = daftarTransaksi.cariById(id);
                TabelHelper.cetakDetailTransaksi(trx);
                if (trx.getInvoice() != null) {
                    trx.getInvoice().cetakInvoice();
                }
            }
        }
    }

    private void prosesPersetujuanTransaksi()
            throws InputTidakValidException, BarangTidakTersediaException,
                   BarangTidakDitemukanException {
        List<Transaksi> menunggu = daftarTransaksi.getTransaksiByStatus(StatusTransaksi.MENUNGGU);
        TabelHelper.cetakTabelTransaksi("TRANSAKSI MENUNGGU PERSETUJUAN", menunggu);
        if (menunggu.isEmpty()) {
            return;
        }

        String id = InputHelper.bacaString(scanner,
                "Masukkan ID Transaksi (0 untuk batal) : ");
        if (id.isEmpty() || id.equals("0")) {
            System.out.println("Proses persetujuan dibatalkan.");
            return;
        }

        Transaksi trx = daftarTransaksi.cariById(id);
        if (trx.getStatus() != StatusTransaksi.MENUNGGU) {
            throw new InputTidakValidException(
                    "Transaksi " + trx.getId() + " sudah berstatus " + trx.getStatus() + ".");
        }

        TabelHelper.cetakDetailTransaksi(trx);
        System.out.println("1. Terima Transaksi");
        System.out.println("2. Tolak Transaksi");
        System.out.println("0. Batal");
        int aksi = InputHelper.bacaInt(scanner, "Pilih tindakan : ");

        if (aksi == 1) {
            admin.terimaTransaksi(trx, daftarBarang);
            System.out.println("Transaksi " + trx.getId()
                    + " DITERIMA. Status laptop diperbarui menjadi DISEWA.");
            trx.getInvoice().cetakInvoice();
        } else if (aksi == 2) {
            admin.tolakTransaksi(trx);
            System.out.println("Transaksi " + trx.getId() + " DITOLAK.");
        } else if (aksi == 0) {
            System.out.println("Tindakan dibatalkan.");
        } else {
            throw new InputTidakValidException("Pilihan tindakan tidak valid.");
        }
    }

    private void konfirmasiPengembalian() throws InputTidakValidException {
        List<Transaksi> aktif = daftarTransaksi.getTransaksiByStatus(StatusTransaksi.DITERIMA);
        TabelHelper.cetakTabelTransaksi("TRANSAKSI AKTIF (MENUNGGU PENGEMBALIAN)", aktif);
        if (aktif.isEmpty()) {
            return;
        }

        String id = InputHelper.bacaString(scanner,
                "Masukkan ID Transaksi yang dikembalikan (0 untuk batal) : ");
        if (id.isEmpty() || id.equals("0")) {
            System.out.println("Konfirmasi pengembalian dibatalkan.");
            return;
        }

        Transaksi trx = daftarTransaksi.cariById(id);
        if (trx.getStatus() != StatusTransaksi.DITERIMA) {
            throw new InputTidakValidException(
                    "Transaksi " + trx.getId() + " tidak berstatus DITERIMA.");
        }

        TabelHelper.cetakDetailTransaksi(trx);
        System.out.println("Tanggal Sewa        : " + FormatHelper.formatTanggal(trx.getTanggalSewa()));
        System.out.println("Tanggal Jatuh Tempo : "
                + FormatHelper.formatTanggal(trx.getTanggalJatuhTempo()));

        LocalDate tanggalKembali = InputHelper.bacaTanggal(
                scanner, "Masukkan Tanggal Kembali (yyyy-MM-dd) : ");

        admin.konfirmasiKembali(trx, tanggalKembali);
        System.out.println("Pengembalian transaksi " + trx.getId()
                + " dikonfirmasi. Seluruh laptop kembali TERSEDIA.");
        trx.getInvoice().cetakInvoice();
    }
}

class MenuPenyewa {

    public static final int AKSI_LOGOUT = 1;

    public static final int AKSI_KELUAR = 2;

    private final Penyewa penyewa;
    private final DaftarBarang daftarBarang;
    private final DaftarTransaksi daftarTransaksi;
    private final Scanner scanner;

    public MenuPenyewa(Penyewa penyewa, DaftarBarang daftarBarang,
            DaftarTransaksi daftarTransaksi, Scanner scanner) {
        this.penyewa = penyewa;
        this.daftarBarang = daftarBarang;
        this.daftarTransaksi = daftarTransaksi;
        this.scanner = scanner;
    }

    public int tampilkanMenu() {
        while (true) {
            System.out.println("\n=== MENU PENYEWA (" + penyewa.getNama() + ") ===");
            System.out.println("1. Lihat Laptop Tersedia");
            System.out.println("2. Tambah ke Daftar Sewa");
            System.out.println("3. Lihat / Hapus dari Daftar Sewa");
            System.out.println("4. Buat Transaksi Sewa");
            System.out.println("5. Riwayat Sewa");
            System.out.println("6. Keluar Program");
            System.out.println("0. Logout");

            try {
                String inputMenu = InputHelper.bacaBarisAtauNull(scanner, "Pilih menu : ");
                if (inputMenu == null) {
                    return AKSI_KELUAR;
                }
                int pilihan = InputHelper.parseInt(inputMenu);
                switch (pilihan) {
                    case 1:
                        lihatLaptopTersedia();
                        break;
                    case 2:
                        tambahKeDaftarSewa();
                        break;
                    case 3:
                        lihatDanHapusDaftarSewa();
                        break;
                    case 4:
                        buatTransaksiSewa();
                        break;
                    case 5:
                        lihatRiwayatSewa();
                        break;
                    case 6:
                        return AKSI_KELUAR;
                    case 0:
                        System.out.println("Logout berhasil.");
                        return AKSI_LOGOUT;
                    default:
                        throw new InputTidakValidException(
                                "Pilihan menu tidak valid. Pilih angka 0 sampai 6.");
                }
            } catch (InputTidakValidException
                    | BarangTidakDitemukanException
                    | BarangTidakTersediaException e) {
                System.out.println("[Kesalahan] " + e.getMessage());
            }
        }
    }

    private void lihatLaptopTersedia() {
        List<Barang> tersedia = penyewa.lihatBarang(daftarBarang);
        TabelHelper.cetakTabelBarang("DAFTAR LAPTOP TERSEDIA", tersedia);
    }

    private void tambahKeDaftarSewa()
            throws BarangTidakDitemukanException, BarangTidakTersediaException,
                   InputTidakValidException {
        lihatLaptopTersedia();
        if (daftarBarang.getBarangTersedia().isEmpty()) {
            return;
        }
        String id = InputHelper.bacaStringWajib(
                scanner, "Masukkan ID laptop yang ingin disewa : ", "ID laptop");
        Barang barang = daftarBarang.cariById(id);
        penyewa.getKeranjang().tambah(barang);
        System.out.println("Laptop " + barang.getId() + " (" + barang.getNama()
                + ") ditambahkan ke daftar sewa.");
    }

    private void lihatDanHapusDaftarSewa()
            throws BarangTidakDitemukanException, InputTidakValidException {
        KeranjangSewa keranjang = penyewa.getKeranjang();
        TabelHelper.cetakTabelBarang("DAFTAR SEWA (KERANJANG)", keranjang.getDaftarBarang());
        if (keranjang.isEmpty()) {
            return;
        }

        System.out.println("Jumlah item : " + keranjang.size());
        System.out.println("1. Hapus laptop dari daftar sewa");
        System.out.println("0. Kembali");
        int pilihan = InputHelper.bacaInt(scanner, "Pilih tindakan : ");
        if (pilihan == 1) {
            String id = InputHelper.bacaStringWajib(
                    scanner, "Masukkan ID laptop yang akan dihapus : ", "ID laptop");
            Barang terhapus = keranjang.hapus(id);
            System.out.println("Laptop " + terhapus.getId() + " (" + terhapus.getNama()
                    + ") dihapus dari daftar sewa.");
        } else if (pilihan != 0) {
            throw new InputTidakValidException("Pilihan tidak valid.");
        }
    }

    private void buatTransaksiSewa()
            throws InputTidakValidException, BarangTidakTersediaException,
                   BarangTidakDitemukanException {
        KeranjangSewa keranjang = penyewa.getKeranjang();
        if (keranjang.isEmpty()) {
            throw new InputTidakValidException(
                    "Daftar sewa (keranjang) masih kosong. Tambahkan laptop terlebih dahulu.");
        }

        for (Barang item : keranjang.getDaftarBarang()) {
            Barang aktual = daftarBarang.cariById(item.getId());
            if (!aktual.isTersedia()) {
                throw new BarangTidakTersediaException(
                        "Laptop " + aktual.getId() + " (" + aktual.getNama()
                                + ") sudah berstatus DISEWA. Hapus dari daftar sewa terlebih dahulu.");
            }
        }

        int lamaSewa = InputHelper.bacaInt(
                scanner, "Masukkan lama sewa (1-30 hari) : ");
        if (lamaSewa < Transaksi.LAMA_SEWA_MIN || lamaSewa > Transaksi.LAMA_SEWA_MAX) {
            throw new InputTidakValidException(
                    "Lama sewa harus antara " + Transaksi.LAMA_SEWA_MIN + " sampai "
                            + Transaksi.LAMA_SEWA_MAX + " hari.");
        }

        tampilkanRingkasanBiaya(keranjang.getDaftarBarang(), lamaSewa);

        boolean lanjut = InputHelper.bacaKonfirmasi(
                scanner, "Lanjutkan ke pemilihan metode pembayaran? (y/n) : ");
        if (!lanjut) {
            System.out.println("Pembuatan transaksi dibatalkan.");
            return;
        }

        Pembayaran pembayaran = pilihMetodePembayaran();
        Transaksi trx = penyewa.buatTransaksi(lamaSewa, pembayaran, daftarTransaksi, daftarBarang);
        System.out.println("\nTransaksi " + trx.getId()
                + " berhasil dibuat dengan status " + trx.getStatus() + ".");
        System.out.println("Menunggu persetujuan Admin.");
    }

    private void tampilkanRingkasanBiaya(List<Barang> items, int lamaSewa) {
        System.out.println("\n=== RINGKASAN BIAYA SEWA (" + lamaSewa + " HARI) ===");
        String garis = FormatHelper.garis(74);
        System.out.println(garis);
        System.out.printf("%-9s %-24s %-12s %-12s %13s%n",
                "ID", "Nama Laptop", "Kategori", "Tarif/Hari", "Subtotal");
        System.out.println(garis);

        double total = 0.0;
        for (Barang barang : items) {
            double subtotal = barang.hitungBiaya(lamaSewa);
            total += subtotal;
            System.out.printf("%-9s %-24s %-12s %-12s %13s%n",
                    barang.getId(),
                    FormatHelper.potongTeks(barang.getNama(), 24),
                    barang.getKategori(),
                    FormatHelper.formatRupiah(barang.getTarifHarian()),
                    FormatHelper.formatRupiah(subtotal));
        }
        System.out.println(garis);
        System.out.printf("%-59s %14s%n", "TOTAL BIAYA SEWA", FormatHelper.formatRupiah(total));
        System.out.println(garis);
    }

    private Pembayaran pilihMetodePembayaran() throws InputTidakValidException {
        System.out.println("\nPilih Metode Pembayaran:");
        System.out.println("1. Transfer Bank");
        System.out.println("2. QRIS");
        System.out.println("3. Tunai");
        int pilih = InputHelper.bacaInt(scanner, "Metode (1-3) : ");

        switch (pilih) {
            case 1:
                String norek = InputHelper.bacaStringWajib(
                        scanner, "Masukkan nomor rekening pengirim/tujuan : ", "Nomor rekening");
                return new TransferBank(norek);
            case 2:
                return new QRIS();
            case 3:
                return new Tunai();
            default:
                throw new InputTidakValidException(
                        "Metode pembayaran tidak valid. Pilih 1, 2, atau 3.");
        }
    }

    private void lihatRiwayatSewa() throws InputTidakValidException {
        List<Transaksi> riwayat = penyewa.lihatRiwayat();
        TabelHelper.cetakTabelTransaksi("RIWAYAT SEWA (DITERIMA / SELESAI)", riwayat);

        List<Transaksi> pengajuan = daftarTransaksi.getPengajuanBelumDiterima(penyewa);
        if (!pengajuan.isEmpty()) {
            TabelHelper.cetakTabelTransaksi("PENGAJUAN MENUNGGU / DITOLAK", pengajuan);
        }

        if (riwayat.isEmpty()) {
            return;
        }

        String id = InputHelper.bacaString(scanner,
                "Masukkan ID Transaksi untuk mencetak invoice (Enter untuk kembali) : ");
        if (id.isEmpty()) {
            return;
        }

        Transaksi pilihan = null;
        for (Transaksi trx : riwayat) {
            if (trx.getId().equalsIgnoreCase(id)) {
                pilihan = trx;
                break;
            }
        }
        if (pilihan == null) {
            throw new InputTidakValidException(
                    "Transaksi '" + id.toUpperCase()
                            + "' tidak ditemukan pada riwayat sewa Anda.");
        }
        pilihan.getInvoice().cetakInvoice();
    }
}
