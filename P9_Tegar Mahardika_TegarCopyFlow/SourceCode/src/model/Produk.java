package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Produk merepresentasikan barang dagangan fisik (PRODUCT)
 * maupun jasa fotokopi/jilid (SERVICE) pada sistem Tegar CopyFlow.
 * Memiliki responsibility mengelola metadata produk, memantau batas minimum stok,
 * serta mengorkestrasikan alokasi batch stok berbasis metode FIFO.
 */
public class Produk {
    private int id;
    private String nama;
    private String kategori;
    private String tipe; // "PRODUCT" atau "SERVICE"
    private String satuan;
    private double hargaJual;
    private double biayaPerUnit; // Untuk SERVICE: biaya bahan baku; untuk PRODUCT: dihitung via batch
    private int minStok;
    private boolean aktif;
    private List<BatchStok> daftarBatch;

    /**
     * Constructor untuk tipe barang dagangan fisik (PRODUCT)
     */
    public Produk(int id, String nama, String kategori, String tipe, String satuan, double hargaJual, int minStok) {
        validasiDasar(nama, kategori, satuan, hargaJual);
        if (minStok < 0) {
            throw new IllegalArgumentException("Minimum stok tidak boleh negatif.");
        }
        this.id = id;
        this.nama = nama.trim();
        this.kategori = kategori.trim();
        this.tipe = (tipe != null && tipe.equalsIgnoreCase("SERVICE")) ? "SERVICE" : "PRODUCT";
        this.satuan = satuan.trim();
        this.hargaJual = hargaJual;
        this.biayaPerUnit = 0.0;
        this.minStok = minStok;
        this.aktif = true;
        this.daftarBatch = new ArrayList<>();
    }

    /**
     * Constructor untuk tipe jasa (SERVICE), misalnya fotokopi, jilid, cetak dokumen
     */
    public Produk(int id, String nama, String kategori, String tipe, String satuan, double hargaJual, double biayaPerUnit) {
        validasiDasar(nama, kategori, satuan, hargaJual);
        if (biayaPerUnit < 0) {
            throw new IllegalArgumentException("Biaya per unit tidak boleh negatif.");
        }
        this.id = id;
        this.nama = nama.trim();
        this.kategori = kategori.trim();
        this.tipe = "SERVICE";
        this.satuan = satuan.trim();
        this.hargaJual = hargaJual;
        this.biayaPerUnit = biayaPerUnit;
        this.minStok = 0;
        this.aktif = true;
        this.daftarBatch = new ArrayList<>();
    }

    private void validasiDasar(String nama, String kategori, String satuan, double hargaJual) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama produk tidak boleh kosong.");
        }
        if (kategori == null || kategori.trim().isEmpty()) {
            throw new IllegalArgumentException("Kategori produk tidak boleh kosong.");
        }
        if (satuan == null || satuan.trim().isEmpty()) {
            throw new IllegalArgumentException("Satuan produk tidak boleh kosong.");
        }
        if (hargaJual < 0) {
            throw new IllegalArgumentException("Harga jual tidak boleh negatif.");
        }
    }

    /**
     * Menambahkan batch persediaan baru (pembelian / restock).
     * Hanya berlaku untuk produk fisik (PRODUCT).
     */
    public BatchStok tambahBatchStok(int idBatch, String tanggalMasuk, int jumlah, double hargaBeli, String catatan) {
        if (!isProductType()) {
            throw new IllegalStateException("Layanan jasa (SERVICE) tidak memerlukan batch persediaan fisik.");
        }
        BatchStok batchBaru = new BatchStok(idBatch, tanggalMasuk, jumlah, hargaBeli, catatan);
        this.daftarBatch.add(batchBaru);
        return batchBaru;
    }

    /**
     * Mengalokasikan stok untuk penjualan menggunakan metode FIFO (First In, First Out).
     * Mengurangi unit dari batch stok yang masuk paling awal terlebih dahulu.
     * @param jumlahMinta jumlah unit yang akan dikeluarkan
     * @return total HPP (Cost of Goods Sold) dari unit-unit yang dialokasikan
     */
    public double alokasikanStokFifo(int jumlahMinta) {
        if (!this.aktif) {
            throw new IllegalStateException("Produk '" + this.nama + "' sedang non-aktif dan tidak dapat ditransaksikan.");
        }
        if (jumlahMinta <= 0) {
            throw new IllegalArgumentException("Jumlah permintaan stok harus lebih dari 0.");
        }

        // Jika produk berupa SERVICE (jasa), stok tidak dibatasi batch fisik
        if (!isProductType()) {
            return jumlahMinta * this.biayaPerUnit;
        }

        int totalTersedia = hitungTotalStok();
        if (totalTersedia < jumlahMinta) {
            throw new IllegalStateException(String.format(
                    "Stok '%s' tidak mencukupi! Tersedia: %d %s, Diminta: %d %s.",
                    this.nama, totalTersedia, this.satuan, jumlahMinta, this.satuan));
        }

        int sisaKebutuhan = jumlahMinta;
        double totalHpp = 0.0;

        for (BatchStok batch : this.daftarBatch) {
            if (sisaKebutuhan <= 0) break;
            if (batch.getSisaStok() > 0) {
                int diambil = batch.ambilStok(sisaKebutuhan);
                totalHpp += (diambil * batch.getHargaBeliPerUnit());
                sisaKebutuhan -= diambil;
            }
        }

        return totalHpp;
    }

    /**
     * Mengembalikan stok ke batch persediaan jika transaksi penjualan dibatalkan (rollback).
     * @param jumlahKembali jumlah unit yang akan dikembalikan
     */
    public void kembalikanStok(int jumlahKembali) {
        if (!isProductType() || jumlahKembali <= 0) {
            return;
        }

        int sisaKembali = jumlahKembali;
        // Kembalikan ke batch dari yang paling baru ke lama
        for (int i = this.daftarBatch.size() - 1; i >= 0 && sisaKembali > 0; i--) {
            BatchStok batch = this.daftarBatch.get(i);
            int ruangKosong = batch.getJumlahAwal() - batch.getSisaStok();
            if (ruangKosong > 0) {
                int masuk = Math.min(ruangKosong, sisaKembali);
                batch.kembalikanStok(masuk);
                sisaKembali -= masuk;
            }
        }
    }

    /**
     * Menghitung total sisa stok dari seluruh batch aktif.
     */
    public int hitungTotalStok() {
        if (!isProductType()) {
            return 0;
        }
        int total = 0;
        for (BatchStok batch : this.daftarBatch) {
            total += batch.getSisaStok();
        }
        return total;
    }

    /**
     * Menghitung total nilai aset persediaan produk berdasarkan HPP batch masing-masing.
     */
    public double hitungNilaiInventaris() {
        if (!isProductType()) {
            return 0.0;
        }
        double totalNilai = 0.0;
        for (BatchStok batch : this.daftarBatch) {
            totalNilai += batch.hitungNilaiSisa();
        }
        return totalNilai;
    }

    /**
     * Menentukan status kesehatan stok produk: AMAN, MENIPIS, HABIS, atau SERVICE.
     */
    public String getStatusStok() {
        if (!isProductType()) {
            return "SERVICE";
        }
        int stok = hitungTotalStok();
        if (stok <= 0) {
            return "HABIS";
        } else if (stok <= this.minStok) {
            return "MENIPIS";
        } else {
            return "AMAN";
        }
    }

    /**
     * Mengubah harga jual dengan validasi state non-negatif.
     */
    public void ubahHargaJual(double hargaBaru, String catatan) {
        if (hargaBaru < 0) {
            throw new IllegalArgumentException("Harga jual baru tidak boleh negatif.");
        }
        this.hargaJual = hargaBaru;
    }

    public boolean isProductType() {
        return "PRODUCT".equalsIgnoreCase(this.tipe);
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    // Encapsulation Getters
    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getKategori() {
        return kategori;
    }

    public String getTipe() {
        return tipe;
    }

    public String getSatuan() {
        return satuan;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public double getBiayaPerUnit() {
        return biayaPerUnit;
    }

    public int getMinStok() {
        return minStok;
    }

    public boolean isAktif() {
        return aktif;
    }

    public List<BatchStok> getDaftarBatch() {
        return Collections.unmodifiableList(daftarBatch);
    }

    @Override
    public String toString() {
        if (isProductType()) {
            return String.format("[%s (ID:%d) | Kat: %s | Stok: %d %s [%s] | Nilai: Rp %,.0f | Harga: Rp %,.0f]",
                    nama, id, kategori, hitungTotalStok(), satuan, getStatusStok(), hitungNilaiInventaris(), hargaJual);
        } else {
            return String.format("[%s (ID:%d) | Tipe: SERVICE | Satuan: %s | HPP: Rp %,.0f | Harga: Rp %,.0f]",
                    nama, id, satuan, biayaPerUnit, hargaJual);
        }
    }
}
