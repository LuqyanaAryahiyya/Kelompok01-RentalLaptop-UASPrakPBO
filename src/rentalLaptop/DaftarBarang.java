package com.rentallaptop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaftarBarang {

    private final ArrayList<Barang> daftarBarang;
    private final Map<String, Barang> indeksById;

    public DaftarBarang() {
        this.daftarBarang = new ArrayList<>();
        this.indeksById = new HashMap<>();
        inisialisasiDataAwal();
    }

    private void inisialisasiDataAwal() {
        try {
            tambahBarang(new LaptopStandard("STD-001", "ASUS VivoBook",
                    "Intel Core i5-1235U, 8GB RAM, 512GB SSD"));
            tambahBarang(new LaptopStandard("STD-002", "Acer Aspire",
                    "Intel Core i5-1135G7, 8GB RAM, 512GB SSD"));
            tambahBarang(new LaptopStandard("STD-003", "Lenovo IdeaPad",
                    "AMD Ryzen 5 5500U, 8GB RAM, 512GB SSD"));
            tambahBarang(new LaptopStandard("STD-004", "HP 15s",
                    "Intel Core i3-1215U, 8GB RAM, 256GB SSD"));
            tambahBarang(new LaptopStandard("STD-005", "Dell Inspiron",
                    "Intel Core i5-1335U, 16GB RAM, 512GB SSD"));

            tambahBarang(new LaptopGaming("GMG-001", "ASUS TUF",
                    "AMD Ryzen 7 6800H, 16GB RAM, RTX 3060, 512GB SSD"));
            tambahBarang(new LaptopGaming("GMG-002", "Lenovo LOQ",
                    "Intel Core i5-13420H, 16GB RAM, RTX 4050, 512GB SSD"));
            tambahBarang(new LaptopGaming("GMG-003", "Acer Nitro",
                    "Intel Core i5-12500H, 16GB RAM, RTX 3050, 512GB SSD"));
            tambahBarang(new LaptopGaming("GMG-004", "HP Victus",
                    "AMD Ryzen 5 7640HS, 16GB RAM, RTX 4050, 512GB SSD"));
            tambahBarang(new LaptopGaming("GMG-005", "MSI Katana",
                    "Intel Core i7-13620H, 16GB RAM, RTX 4060, 1TB SSD"));
          
            tambahBarang(new LaptopWorkstation("WRK-001", "Dell Precision",
                    "Intel Core i7-13800H, 32GB RAM, RTX A2000, 1TB SSD"));
            tambahBarang(new LaptopWorkstation("WRK-002", "HP ZBook",
                    "Intel Core i7-12800H, 32GB RAM, RTX A1000, 1TB SSD"));
            tambahBarang(new LaptopWorkstation("WRK-003", "Lenovo ThinkPad P Series",
                    "Intel Core i9-13900H, 32GB RAM, RTX 2000 Ada, 1TB SSD"));
            tambahBarang(new LaptopWorkstation("WRK-004", "ASUS ProArt Studiobook",
                    "Intel Core i9-13980HX, 32GB RAM, RTX 4070, 1TB SSD"));
            tambahBarang(new LaptopWorkstation("WRK-005", "MSI CreatorPro",
                    "Intel Core i7-13700H, 32GB RAM, RTX A3000, 1TB SSD"));
        } catch (InputTidakValidException e) {
            throw new IllegalStateException("Gagal menginisialisasi data awal laptop.", e);
        }
    }

    public String generateId(String prefix) {
        String p = prefix.trim().toUpperCase();
        int maxUrut = 0;
        for (Barang barang : daftarBarang) {
            String id = barang.getId();
            if (id.startsWith(p + "-")) {
                String bagianAngka = id.substring(p.length() + 1);
                try {
                    int angka = Integer.parseInt(bagianAngka);
                    if (angka > maxUrut) {
                        maxUrut = angka;
                    }
                } catch (NumberFormatException ignored) {
                  
                }
            }
        }
        String kandidat;
        do {
            maxUrut++;
            kandidat = String.format("%s-%03d", p, maxUrut);
        } while (indeksById.containsKey(kandidat));
        return kandidat;
    }

    public void tambahBarang(Barang barang) throws InputTidakValidException {
        if (barang == null) {
            throw new InputTidakValidException("Data barang tidak boleh kosong.");
        }
        String id = barang.getId().toUpperCase();
        if (indeksById.containsKey(id)) {
            throw new InputTidakValidException(
                    "ID laptop '" + id + "' sudah digunakan. Gunakan ID lain.");
        }
        daftarBarang.add(barang);
        indeksById.put(id, barang);
    }
  
    public void ubahBarang(String id, String namaBaru, Double tarifBaru, String spesifikasiBaru)
            throws BarangTidakDitemukanException, InputTidakValidException {
        Barang barang = cariById(id);
        if (namaBaru != null && !namaBaru.trim().isEmpty()) {
            barang.setNama(namaBaru);
        }
        if (tarifBaru != null) {
            barang.setTarifHarian(tarifBaru);
        }
        if (spesifikasiBaru != null && !spesifikasiBaru.trim().isEmpty() && barang instanceof Laptop) {
            ((Laptop) barang).setSpesifikasi(spesifikasiBaru);
        }
    }

    public Barang hapusBarang(String id)
            throws BarangTidakDitemukanException, BarangTidakTersediaException {
        Barang barang = cariById(id);
        if (!barang.isTersedia()) {
            throw new BarangTidakTersediaException(
                    "Laptop " + barang.getId() + " (" + barang.getNama()
                            + ") sedang DISEWA dan tidak boleh dihapus.");
        }
        daftarBarang.remove(barang);
        indeksById.remove(barang.getId());
        return barang;
    }

    public Barang cariById(String id) throws BarangTidakDitemukanException {
        if (id == null || id.trim().isEmpty()) {
            throw new BarangTidakDitemukanException("ID laptop tidak boleh kosong.");
        }
        String kunci = id.trim().toUpperCase();
        Barang hasil = indeksById.get(kunci);
        if (hasil == null) {
            throw new BarangTidakDitemukanException(
                    "Laptop dengan ID '" + kunci + "' tidak ditemukan.");
        }
        return hasil;
    }

    public List<Barang> getSemuaBarang() {
        return new ArrayList<>(daftarBarang);
    }

    public List<Barang> getBarangTersedia() {
        List<Barang> hasil = new ArrayList<>();
        for (Barang barang : daftarBarang) {
            if (barang.isTersedia()) {
                hasil.add(barang);
            }
        }
        return hasil;
    }
}

class KeranjangSewa {

    private final ArrayList<Barang> items;

    public KeranjangSewa() {
        this.items = new ArrayList<>();
    }

    /**
     * Menambahkan laptop ke keranjang sewa.
     * Laptop hanya bisa ditambahkan jika berstatus {@code TERSEDIA} dan belum ada di keranjang.
     */
    public void tambah(Barang barang)
            throws BarangTidakTersediaException, InputTidakValidException {
        if (barang == null) {
            throw new InputTidakValidException("Barang tidak boleh kosong.");
        }
        if (!barang.isTersedia()) {
            throw new BarangTidakTersediaException(
                    "Laptop " + barang.getId() + " (" + barang.getNama()
                            + ") sedang DISEWA dan tidak dapat ditambahkan.");
        }
        for (Barang item : items) {
            if (item.getId().equalsIgnoreCase(barang.getId())) {
                throw new InputTidakValidException(
                        "Laptop " + barang.getId() + " sudah ada di daftar sewa Anda.");
            }
        }
        items.add(barang);
    }

    /** Menghapus laptop dari keranjang sewa berdasarkan ID. */
    public Barang hapus(String idBarang) throws BarangTidakDitemukanException {
        if (idBarang == null || idBarang.trim().isEmpty()) {
            throw new BarangTidakDitemukanException("ID laptop tidak boleh kosong.");
        }
        String kunci = idBarang.trim().toUpperCase();
        for (int i = 0; i < items.size(); i++) {
            Barang item = items.get(i);
            if (item.getId().equalsIgnoreCase(kunci)) {
                return items.remove(i);
            }
        }
        throw new BarangTidakDitemukanException(
                "Laptop dengan ID '" + kunci + "' tidak ada di daftar sewa Anda.");
    }

    public List<Barang> getDaftarBarang() {
        return Collections.unmodifiableList(items);
    }

    /** Menghitung estimasi total biaya sewa untuk seluruh laptop di keranjang pada lama sewa tertentu. */
    public double hitungEstimasiTotal(int lamaSewa) {
        double total = 0.0;
        for (Barang barang : items) {
            total += barang.hitungBiaya(lamaSewa);
        }
        return total;
    }

    /** Mengosongkan seluruh isi keranjang sewa. */
    public void kosongkan() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Mengembalikan jumlah laptop di dalam keranjang. */
    public int size() {
        return items.size();
    }
}
