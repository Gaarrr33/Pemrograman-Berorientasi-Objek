package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class KatalogProduk bertindak sebagai pengelola kumpulan objek Produk (List<Produk>)
 * pada sistem Tegar CopyFlow.
 * 
 * Tanggung Jawab:
 * - Menampung daftar master produk di memori.
 * - Mengimplementasikan operasi bisnis CRUD (tambah, cari, hapus, tampilkan).
 * - Menjaga enkapsulasi dan aturan keunikan kode.
 * - Menyediakan operasi penggantian atomik gantiSemua() setelah validasi file berhasil.
 */
public class KatalogProduk {
    private final List<Produk> daftarProduk;

    public KatalogProduk() {
        this.daftarProduk = new ArrayList<>();
    }

    public void tambahProduk(Produk produk) {
        if (produk == null) {
            throw new IllegalArgumentException("Objek produk tidak boleh null.");
        }
        if (cariProduk(produk.getKode()) != null) {
            throw new IllegalArgumentException("Produk dengan kode '" + produk.getKode() + "' sudah terdaftar dalam katalog.");
        }
        daftarProduk.add(produk);
    }

    public Produk cariProduk(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            return null;
        }
        for (Produk p : daftarProduk) {
            if (p.getKode().equalsIgnoreCase(kode.trim())) {
                return p;
            }
        }
        return null;
    }

    public boolean hapusProduk(String kode) {
        Produk produk = cariProduk(kode);
        if (produk == null) {
            return false;
        }
        return daftarProduk.remove(produk);
    }

    public void gantiSemua(List<Produk> produkBaru) {
        if (produkBaru == null) {
            throw new IllegalArgumentException("Daftar produk baru tidak boleh null.");
        }
        daftarProduk.clear();
        daftarProduk.addAll(produkBaru);
    }

    public void tampilkanKatalog() {
        if (daftarProduk.isEmpty()) {
            System.out.println("Katalog produk toko masih kosong.");
            return;
        }

        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf(" %-9s | %-4s | %-28s | %-9s | %-10s | %-9s | %-10s | %-14s\n",
                "Kode", "ID", "Nama Produk / Layanan", "Tipe", "Stok Sisa", "Status", "Harga Jual", "Nilai Aset");
        System.out.println("----------------------------------------------------------------------------------------------------");

        for (Produk p : daftarProduk) {
            String stokStr = p.isProductType() ? (p.hitungTotalStok() + " " + p.getSatuan()) : "-";
            System.out.printf(" %-9s | %-4d | %-28s | %-9s | %-10s | %-9s | Rp %,8.0f | Rp %,12.0f\n",
                    p.getKode(),
                    p.getId(),
                    p.getNama(),
                    p.getTipe(),
                    stokStr,
                    p.getStatusStok(),
                    p.getHargaJual(),
                    p.hitungNilaiInventaris());
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf(" Total Item di Katalog: %d produk | Total Nilai Inventaris Toko: Rp %,.0f\n",
                daftarProduk.size(), hitungTotalNilaiKatalog());
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    public double hitungTotalNilaiKatalog() {
        double total = 0.0;
        for (Produk p : daftarProduk) {
            total += p.hitungNilaiInventaris();
        }
        return total;
    }

    public int getJumlahProduk() {
        return daftarProduk.size();
    }

    public List<Produk> getDaftarProduk() {
        return Collections.unmodifiableList(daftarProduk);
    }
}
