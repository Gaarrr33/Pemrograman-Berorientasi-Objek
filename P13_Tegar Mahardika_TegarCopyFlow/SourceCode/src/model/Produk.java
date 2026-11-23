package model;

import exception.StokTidakCukupException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Produk merepresentasikan barang dagangan fisik (PRODUCT)
 * maupun jasa fotokopi/jilid (SERVICE) pada sistem Tegar CopyFlow.
 * 
 * Dilengkapi dengan:
 * - Atribut kode produk sebagai identitas unik tetap (immutable).
 * - Metode toFileLine() untuk serialisasi ke format file teks persistensi.
 * - Constructor lengkap untuk merekonstruksi objek secara presisi dari file.
 */
public class Produk {
    private final String kode; // Identitas unik (misal: "PRD-001", "SRV-001")
    private int id;
    private String nama;
    private String kategori;
    private String tipe; // "PRODUCT" atau "SERVICE"
    private String satuan;
    private double hargaJual;
    private double biayaPerUnit; // Untuk SERVICE: biaya bahan baku; untuk PRODUCT: dihitung via batch
    private int minStok;
    private boolean aktif;
    private final List<BatchStok> daftarBatch; // Collection internal batch FIFO

    /**
     * Constructor untuk tipe barang dagangan fisik (PRODUCT)
     */
    public Produk(String kode, int id, String nama, String kategori, String tipe, String satuan, double hargaJual, int minStok) {
        this(kode, id, nama, kategori, tipe, satuan, hargaJual, minStok, 0.0, true);
    }

    /**
     * Constructor untuk tipe jasa (SERVICE), misalnya fotokopi, jilid
     */
    public Produk(String kode, int id, String nama, String kategori, String tipe, String satuan, double hargaJual, double biayaPerUnit) {
        this(kode, id, nama, kategori, "SERVICE", satuan, hargaJual, 0, biayaPerUnit, true);
    }

    /**
     * Constructor lengkap untuk pembentukan kembali objek dari media penyimpanan file
     */
    public Produk(String kode, int id, String nama, String kategori, String tipe, String satuan, double hargaJual, int minStok, double biayaPerUnit, boolean aktif) {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode produk tidak boleh kosong.");
        }
        if (kode.contains("|") || kode.contains("\n") || kode.contains("\r")) {
            throw new IllegalArgumentException("Kode produk tidak boleh mengandung karakter pemisah '|' atau baris baru.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID produk harus bilangan bulat positif (> 0).");
        }
        validasiDasar(nama, kategori, satuan, hargaJual);
        if (minStok < 0) {
            throw new IllegalArgumentException("Minimum stok tidak boleh negatif.");
        }
        if (biayaPerUnit < 0) {
            throw new IllegalArgumentException("Biaya per unit tidak boleh negatif.");
        }

        this.kode = kode.trim().toUpperCase();
        this.id = id;
        this.nama = nama.trim();
        this.kategori = kategori.trim();
        this.tipe = (tipe != null && tipe.equalsIgnoreCase("SERVICE")) ? "SERVICE" : "PRODUCT";
        this.satuan = satuan.trim();
        this.hargaJual = hargaJual;
        this.minStok = minStok;
        this.biayaPerUnit = biayaPerUnit;
        this.aktif = aktif;
        this.daftarBatch = new ArrayList<>();
    }

    private void validasiDasar(String nama, String kategori, String satuan, double hargaJual) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama produk tidak boleh kosong.");
        }
        if (nama.contains("|") || nama.contains("\n") || nama.contains("\r")) {
            throw new IllegalArgumentException("Nama produk tidak boleh mengandung karakter pemisah '|' atau baris baru.");
        }
        if (kategori == null || kategori.trim().isEmpty()) {
            throw new IllegalArgumentException("Kategori produk tidak boleh kosong.");
        }
        if (kategori.contains("|") || kategori.contains("\n") || kategori.contains("\r")) {
            throw new IllegalArgumentException("Kategori produk tidak boleh mengandung karakter pemisah '|' atau baris baru.");
        }
        if (satuan == null || satuan.trim().isEmpty()) {
            throw new IllegalArgumentException("Satuan produk tidak boleh kosong.");
        }
        if (satuan.contains("|") || satuan.contains("\n") || satuan.contains("\r")) {
            throw new IllegalArgumentException("Satuan produk tidak boleh mengandung karakter pemisah '|' atau baris baru.");
        }
        if (hargaJual < 0) {
            throw new IllegalArgumentException("Harga jual tidak boleh bernilai negatif.");
        }
    }

    /**
     * Mengonversi data objek ke baris string dengan pemisah '|' untuk persistensi file.
     * Menggunakan Locale.US agar angka desimal selalu menggunakan titik '.' (locale-independent).
     * Format: kode|id|nama|kategori|tipe|satuan|hargaJual|minStok|biayaPerUnit|aktif
     */
    public String toFileLine() {
        double col8 = isProductType() ? (double) hitungTotalStok() : biayaPerUnit;
        return String.format(java.util.Locale.US, "%s|%d|%s|%s|%s|%s|%.2f|%d|%.2f|%b",
                kode, id, nama, kategori, tipe, satuan, hargaJual, minStok, col8, aktif);
    }

    public BatchStok tambahBatchStok(int idBatch, String tanggalMasuk, int jumlah, double hargaBeli, String catatan) {
        if (!isProductType()) {
            throw new IllegalStateException("Layanan jasa (SERVICE) tidak memerlukan batch persediaan fisik.");
        }
        BatchStok batchBaru = new BatchStok(idBatch, tanggalMasuk, jumlah, hargaBeli, catatan);
        this.daftarBatch.add(batchBaru);
        return batchBaru;
    }

    public double alokasikanStokFifo(int jumlahMinta) throws StokTidakCukupException {
        if (!this.aktif) {
            throw new IllegalStateException("Produk '" + this.nama + "' sedang non-aktif dan tidak dapat ditransaksikan.");
        }
        if (jumlahMinta <= 0) {
            throw new IllegalArgumentException("Jumlah permintaan stok harus lebih dari 0.");
        }

        if (!isProductType()) {
            return jumlahMinta * this.biayaPerUnit;
        }

        int totalTersedia = hitungTotalStok();
        if (jumlahMinta > totalTersedia) {
            throw new StokTidakCukupException(this.nama, totalTersedia, jumlahMinta, this.satuan);
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

    public void kembalikanStok(int jumlahKembali) {
        if (!isProductType() || jumlahKembali <= 0) {
            return;
        }

        int sisaKembali = jumlahKembali;
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

    public void ubahHargaJual(double hargaBaru, String alasan) {
        if (hargaBaru < 0) {
            throw new IllegalArgumentException("Harga jual baru tidak boleh negatif.");
        }
        this.hargaJual = hargaBaru;
    }

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

    public boolean isProductType() {
        return "PRODUCT".equalsIgnoreCase(this.tipe);
    }

    // Encapsulation Getters & Setters
    public String getKode() {
        return kode;
    }

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

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    public List<BatchStok> getDaftarBatch() {
        return Collections.unmodifiableList(daftarBatch);
    }

    @Override
    public String toString() {
        if (isProductType()) {
            return String.format("[%s | %s (ID:%d) | Kat: %s | Stok: %d %s [%s] | Nilai: Rp %,.0f | Harga: Rp %,.0f]",
                    kode, nama, id, kategori, hitungTotalStok(), satuan, getStatusStok(), hitungNilaiInventaris(), hargaJual);
        } else {
            return String.format("[%s | %s (ID:%d) | Tipe: SERVICE | Satuan: %s | HPP: Rp %,.0f | Harga: Rp %,.0f]",
                    kode, nama, id, satuan, biayaPerUnit, hargaJual);
        }
    }
}
