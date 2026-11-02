package model;

/**
 * Class BatchStok merepresentasikan satu gelombang pengadaan (batch) persediaan barang fisik
 * dengan harga beli (unit cost) dan tanggal masuk tertentu.
 * Mendukung kalkulasi valuasi inventaris dan alokasi stok FIFO sesuai sistem Tegar CopyFlow.
 * 
 * Menjamin integritas state:
 * - Sisa stok tidak boleh minus.
 * - Sisa stok saat rollback tidak boleh melampaui jumlah kulakan awal.
 */
public class BatchStok {
    private int id;
    private String tanggalMasuk;
    private int jumlahAwal;
    private int sisaStok;
    private double hargaBeliPerUnit;
    private String catatan;

    public BatchStok(int id, String tanggalMasuk, int jumlahAwal, double hargaBeliPerUnit, String catatan) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID batch harus berupa bilangan bulat positif (> 0).");
        }
        if (jumlahAwal <= 0) {
            throw new IllegalArgumentException("Jumlah kuantitas awal batch stok harus lebih dari 0.");
        }
        if (hargaBeliPerUnit < 0) {
            throw new IllegalArgumentException("Harga beli per unit (kulakan) tidak boleh negatif.");
        }
        this.id = id;
        this.tanggalMasuk = (tanggalMasuk != null && !tanggalMasuk.trim().isEmpty()) ? tanggalMasuk.trim() : "2026-11-02";
        this.jumlahAwal = jumlahAwal;
        this.sisaStok = jumlahAwal;
        this.hargaBeliPerUnit = hargaBeliPerUnit;
        this.catatan = (catatan != null) ? catatan.trim() : "Batch Stok";
    }

    /**
     * Mengambil stok dari batch ini dengan batas maksimal sisa yang tersedia.
     * Pemeriksaan dilakukan sebelum mutasi state.
     * @param jumlahMinta jumlah unit yang diminta
     * @return unit aktual yang terpotong dari batch ini
     */
    public int ambilStok(int jumlahMinta) {
        if (jumlahMinta <= 0) {
            throw new IllegalArgumentException("Jumlah pengambilan stok harus lebih dari 0.");
        }
        if (this.sisaStok <= 0) {
            return 0;
        }

        int alokasi = Math.min(this.sisaStok, jumlahMinta);
        this.sisaStok -= alokasi;
        return alokasi;
    }

    /**
     * Mengembalikan stok ke dalam batch jika terjadi pembatalan transaksi (rollback).
     * State dijaga secara konsisten agar sisaStok tidak melebihi jumlahAwal batch.
     * @param jumlahKembali jumlah unit yang dikembalikan
     */
    public void kembalikanStok(int jumlahKembali) {
        if (jumlahKembali <= 0) {
            throw new IllegalArgumentException("Jumlah pengembalian stok harus lebih dari 0.");
        }
        if (this.sisaStok + jumlahKembali > this.jumlahAwal) {
            this.sisaStok = this.jumlahAwal;
        } else {
            this.sisaStok += jumlahKembali;
        }
    }

    /**
     * Menghitung nilai rupiah sisa persediaan batch ini.
     */
    public double hitungNilaiSisa() {
        return this.sisaStok * this.hargaBeliPerUnit;
    }

    // Encapsulation Getters
    public int getId() {
        return id;
    }

    public String getTanggalMasuk() {
        return tanggalMasuk;
    }

    public int getJumlahAwal() {
        return jumlahAwal;
    }

    public int getSisaStok() {
        return sisaStok;
    }

    public double getHargaBeliPerUnit() {
        return hargaBeliPerUnit;
    }

    public String getCatatan() {
        return catatan;
    }

    @Override
    public String toString() {
        return String.format("[Batch #%d | Masuk: %s | Sisa: %d/%d unit | Beli: Rp %,.0f | Ket: %s]",
                id, tanggalMasuk, sisaStok, jumlahAwal, hargaBeliPerUnit, catatan);
    }
}

