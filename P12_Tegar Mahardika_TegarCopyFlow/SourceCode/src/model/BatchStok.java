package model;

/**
 * Class BatchStok merepresentasikan satu gelombang pengadaan (batch) persediaan barang fisik
 * dengan harga beli (unit cost) dan tanggal masuk tertentu.
 * Digunakan untuk mendukung kalkulasi valuasi inventaris dan alokasi stok FIFO.
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
        this.tanggalMasuk = (tanggalMasuk != null && !tanggalMasuk.trim().isEmpty()) ? tanggalMasuk.trim() : "2026-11-11";
        this.jumlahAwal = jumlahAwal;
        this.sisaStok = jumlahAwal;
        this.hargaBeliPerUnit = hargaBeliPerUnit;
        this.catatan = (catatan != null) ? catatan.trim() : "Batch Stok";
    }

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

    public double hitungNilaiSisa() {
        return this.sisaStok * this.hargaBeliPerUnit;
    }

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

