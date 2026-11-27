# Rental Laptop (CLI)

Aplikasi penyewaan laptop berbasis Command Line Interface (CLI) menggunakan Java 17 tanpa library eksternal. Proyek ini dikembangkan untuk memenuhi UAS Praktikum Pemrograman Berorientasi Objek (PBO) dengan menerapkan *Abstraction*, *Inheritance*, *Polymorphism*, *Encapsulation*, *Interface*, *Collection*, *Exception Handling*, dan dokumentasi *Javadoc*.

## Anggota Kelompok (Kelompok 1)

| No | Nama | NIM |
|---|---|---|
| 1 | Muhammad Haikal Al Fikrah | 250810701100084 |
| 2 | Ahmad Wazir Sahfikri | 250810701100050 |
| 3 | Luqyana Aryahiyya | 250810701100072 |

## Fitur Utama

- **Autentikasi & Peran Pengguna**: Login untuk `Admin` dan `Penyewa` dengan batas maksimal 3 kali percobaan gagal.
- **Manajemen Laptop (Admin)**: Lihat seluruh laptop beserta status, tambah laptop (ID otomatis per kategori atau manual), ubah data laptop, dan hapus laptop (ditolak jika sedang `DISEWA`).
- **Penyewaan Laptop (Penyewa)**: Lihat laptop `TERSEDIA`, kelola daftar sewa (keranjang) berisi banyak laptop lintas kategori, buat transaksi dengan lama sewa 1–30 hari, pilih metode pembayaran (`Transfer Bank`, `QRIS`, atau `Tunai`), dan lihat riwayat sewa beserta invoice.
- **Persetujuan & Pengembalian (Admin)**: Terima atau tolak transaksi `MENUNGGU`, cek ulang ketersediaan laptop saat persetujuan, cetak invoice resmi, dan konfirmasi pengembalian dengan perhitungan denda keterlambatan otomatis (`hariTerlambat * 150% * tarifHarian` per laptop).

## Kategori Laptop & Rumus Biaya

| Kategori | Class | Tarif Default/Hari | Biaya Tambahan | Rumus Biaya Sewa |
|---|---|---|---|---|
| Standard | `LaptopStandard` | Rp50.000 | Tidak ada | `tarif * hari` |
| Gaming | `LaptopGaming` | Rp100.000 | Perawatan Rp15.000/hari | `(tarif + 15.000) * hari` |
| Workstation | `LaptopWorkstation` | Rp150.000 | Asuransi 10% dari total sewa | `(tarif * hari) * 1.10` |

## Akun Bawaan (In-Memory)

| Peran | Username | Password | Nama Tampil |
|---|---|---|---|
| Admin | `admin` | `admin123` | Administrator |
| Penyewa | `penyewa1` | `pass123` | Penyewa Satu |
| Penyewa | `penyewa2` | `pass456` | Penyewa Dua |

## Struktur Proyek

Seluruh class berada di satu package `rentalLaptop` (9 file). Class pendukung ditulis tanpa modifier `public` di dalam file induknya.

```text
.
├─ src/rentalLaptop/
│  ├─ Main.java             Main, MenuAdmin, MenuPenyewa
│  ├─ Akun.java             Akun (abstract), Admin, Penyewa
│  ├─ Barang.java           Barang (abstract), Laptop (abstract), LaptopStandard,
│  │                        LaptopGaming, LaptopWorkstation, StatusBarang (enum)
│  ├─ Transaksi.java        Transaksi, StatusTransaksi (enum), Invoice
│  ├─ Pembayaran.java       Pembayaran (interface), TransferBank, QRIS, Tunai
│  ├─ DaftarBarang.java     DaftarBarang, KeranjangSewa
│  ├─ DaftarTransaksi.java  DaftarTransaksi
│  ├─ Exceptions.java       LoginGagalException, InputTidakValidException,
│  │                        BarangTidakTersediaException, BarangTidakDitemukanException
│  └─ InputHelper.java      InputHelper, FormatHelper, TabelHelper
│  
└─ README.md
```

## Cara Kompilasi dan Menjalankan (JDK 17+)

Semua file `.java` harus dikompilasi sekaligus (jangan hanya `Main.java`), karena beberapa class berada di file induknya.

### Windows (PowerShell)

```powershell
mkdir out -Force | Out-Null
javac --release 17 -encoding UTF-8 -d out src\com\rentallaptop\*.java
java -cp out com.rentallaptop.Main
```

### Linux / macOS (Bash)

```bash
mkdir -p out
javac --release 17 -encoding UTF-8 -d out src/rentalLaptop/*.java
java -cp out rentalLaptop.Main
```

## Generate Javadoc

Flag `-package` diperlukan agar class yang tidak `public` ikut didokumentasikan.

```powershell
mkdir docs\javadoc -Force | Out-Null
javadoc -package -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 -d docs\javadoc -sourcepath src -subpackages com.rentallaptop
```
