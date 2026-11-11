package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class KatalogProduk bertindak sebagai pengelola kumpulan objek Produk (List<Produk>)
 * pada sistem Tegar CopyFlow sesuai prinsip OOP dan Modul 11.
 * 
 * Tanggung Jawab (Responsibility):
 * 1. Mengelola collection internal ArrayList<Produk> secara private dan aman (Encapsulation).
 * 2. Menyediakan operasi bisnis: tambahProduk, cariProduk, hapusProduk, dan tampilkanKatalog.
 * 3. Menjamin keunikan identitas data berdasarkan kode produk (mencegah duplikasi).
 * 4. Mencegah manipulasi langsung dari luar dengan menyediakan unmodifiable collection.
 */
public class KatalogProduk {
    private final List<Produk> daftarProduk;

    public KatalogProduk() {
        this.daftarProduk = new ArrayList<>();
    }

    /**
     * Menambahkan objek Produk ke dalam katalog.
     * Memvalidasi bahwa objek tidak null dan kode produk belum pernah terdaftar.
     */
    public void tambahProduk(Produk produk) {
        if (produk == null) {
            throw new IllegalArgumentException("Objek produk tidak boleh null.");
        }
        if (cariProduk(produk.getKode()) != null) {
            throw new IllegalArgumentException("Produk dengan kode '" + produk.getKode() + "' sudah terdaftar dalam katalog.");
        }
        daftarProduk.add(produk);
    }

    /**
     * Mencari produk berdasarkan kode unik produk.
     * Menggunakan perulangan for-each dan pencocokan case-insensitive.
     * @return objek Produk jika ditemukan, atau null jika tidak ada.
     */
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

    /**
     * Menghapus produk dari katalog berdasarkan kode produk.
     * @return true jika produk ditemukan dan berhasil dihapus, false jika tidak ditemukan.
     */
    public boolean hapusProduk(String kode) {
        Produk produk = cariProduk(kode);
        if (produk == null) {
            return false;
        }
        return daftarProduk.remove(produk);
    }

    /**
     * Menampilkan seluruh isi katalog produk dan ringkasan persediaan toko ke konsol.
     */
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

    /**
     * Menghitung total nilai inventaris seluruh produk dalam katalog.
     */
    public double hitungTotalNilaiKatalog() {
        double total = 0.0;
        for (Produk p : daftarProduk) {
            total += p.hitungNilaiInventaris();
        }
        return total;
    }

    /**
     * Menghitung jumlah produk terdaftar di katalog.
     */
    public int getJumlahProduk() {
        return daftarProduk.size();
    }

    /**
     * Mengembalikan daftar produk yang bersifat Read-Only untuk melindungi enkapsulasi
     * sehingga koleksi internal tidak dapat dimanipulasi secara langsung oleh class lain.
     */
    public List<Produk> getDaftarProduk() {
        return Collections.unmodifiableList(daftarProduk);
    }
}

