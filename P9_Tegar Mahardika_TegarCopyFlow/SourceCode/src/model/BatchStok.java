package model;

/**
 * Class BatchStok merepresentasikan satu batch persediaan barang fisik
 * dengan harga beli (unit cost) dan tanggal masuk tertentu.
 * Digunakan untuk mendukung kalkulasi valuasi inventaris dan alokasi stok FIFO
 * sesuai implementasi backend Tegar CopyFlow (tabel stock_batches).
 */
public class BatchStok {
    private int id;
    private String tanggalMasuk;
    private int jumlahAwal;
    private int sisaStok;
    private double hargaBeliPerUnit;
    private String catatan;

    public BatchStok(int id, String tanggalMasuk, int jumlahAwal, double hargaBeliPerUnit, String catatan) {
        if (jumlahAwal <= 0) {
            throw new IllegalArgumentException("Jumlah awal batch stok harus lebih dari 0.");
        }
        if (hargaBeliPerUnit < 0) {
            throw new IllegalArgumentException("Harga beli per unit tidak boleh negatif.");
        }
        this.id = id;
        this.tanggalMasuk = (tanggalMasuk != null && !tanggalMasuk.trim().isEmpty()) ? tanggalMasuk : "2026-10-26";
        this.jumlahAwal = jumlahAwal;
        this.sisaStok = jumlahAwal;
        this.hargaBeliPerUnit = hargaBeliPerUnit;
        this.catatan = (catatan != null) ? catatan : "Batch Stok";
    }

    /**
     * Mengambil stok dari batch ini dengan batas maksimal sisa yang tersedia.
     * @param jumlahMinta jumlah yang diinginkan
     * @return jumlah unit aktual yang berhasil dialokasikan dari batch ini
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
     * State dijaga agar sisaStok tidak melebihi jumlahAwal batch.
     * @param jumlahKembali jumlah yang dikembalikan
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

    // Getter methods (Encapsulation)
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
