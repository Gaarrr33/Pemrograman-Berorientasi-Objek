/**
 * Class Kunjungan menghubungkan Pasien dengan TenagaMedis yang melayaninya.
 * Mengimplementasikan antarmuka Comparable untuk mendukung Fitur Kreatif:
 * Prioritas antrean berdasarkan tingkat urgensi (1: Darurat, 2: Mendesak, 3: Reguler).
 */
public class Kunjungan implements Comparable<Kunjungan> {
    private String idKunjungan;
    private int nomorAntrean;
    private Pasien pasien;
    private TenagaMedis tenagaMedis;
    private String keluhan;
    private int prioritas; // 1 = Darurat, 2 = Mendesak, 3 = Reguler
    private String status; // "MENUNGGU", "SELESAI", "DIBATALKAN"

    /**
     * Inisialisasi data Kunjungan baru.
     * @param idKunjungan ID unik kunjungan
     * @param nomorAntrean Nomor urut antrean pasien
     * @param pasien Objek Pasien terkait
     * @param tenagaMedis Objek TenagaMedis yang menangani
     * @param keluhan Keluhan sakit yang dialami pasien
     * @param prioritas Tingkat urgensi pasien (1=Darurat, 2=Mendesak, 3=Reguler)
     */
    public Kunjungan(String idKunjungan, int nomorAntrean, Pasien pasien, TenagaMedis tenagaMedis,
                     String keluhan, int prioritas) {
        if (idKunjungan == null || idKunjungan.trim().isEmpty()) {
            throw new IllegalArgumentException("ID Kunjungan tidak boleh kosong!");
        }
        if (pasien == null) {
            throw new IllegalArgumentException("Pasien tidak boleh bernilai null!");
        }
        if (tenagaMedis == null) {
            throw new IllegalArgumentException("Tenaga Medis tidak boleh bernilai null!");
        }
        setPrioritas(prioritas);

        this.idKunjungan = idKunjungan.trim();
        this.nomorAntrean = nomorAntrean;
        this.pasien = pasien;
        this.tenagaMedis = tenagaMedis;
        this.keluhan = (keluhan != null && !keluhan.trim().isEmpty()) ? keluhan.trim() : "Pemeriksaan Rutin";
        this.status = "MENUNGGU";
    }

    // --- Getter dan Setter dengan Validasi (Encapsulation) ---

    public String getIdKunjungan() {
        return idKunjungan;
    }

    public int getNomorAntrean() {
        return nomorAntrean;
    }

    public Pasien getPasien() {
        return pasien;
    }

    public void setPasien(Pasien pasien) {
        if (pasien == null) {
            throw new IllegalArgumentException("Pasien tidak boleh null!");
        }
        this.pasien = pasien;
    }

    public TenagaMedis getTenagaMedis() {
        return tenagaMedis;
    }

    public void setTenagaMedis(TenagaMedis tenagaMedis) {
        if (tenagaMedis == null) {
            throw new IllegalArgumentException("Tenaga Medis tidak boleh null!");
        }
        this.tenagaMedis = tenagaMedis;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

    public int getPrioritas() {
        return prioritas;
    }

    /**
     * Validasi Business Rule: Prioritas hanya bernilai 1 (Darurat), 2 (Mendesak), atau 3 (Reguler).
     */
    public void setPrioritas(int prioritas) {
        if (prioritas < 1 || prioritas > 3) {
            throw new IllegalArgumentException("Tingkat prioritas tidak valid! Gunakan: 1 (Darurat), 2 (Mendesak), atau 3 (Reguler).");
        }
        this.prioritas = prioritas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Mengembalikan teks representasi label prioritas untuk tampilan.
     */
    public String getTeksPrioritas() {
        switch (prioritas) {
            case 1:
                return "1 - DARURAT";
            case 2:
                return "2 - MENDESAK";
            case 3:
                return "3 - REGULER";
            default:
                return "TIDAK DIKETAHUI";
        }
    }

    /**
     * Memproses pelayanan pada kunjungan ini.
     * Business Rule: Kunjungan tidak boleh diproses jika statusnya sudah SELESAI.
     */
    public void prosesPelayanan() {
        if ("SELESAI".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("Pelanggaran Aturan Bisnis: Kunjungan [" + idKunjungan +
                    "] sudah berstatus SELESAI dan tidak dapat diproses ulang!");
        }

        System.out.println("\n>>> MEMPROSES PELAYANAN UNTUK KUNJUNGAN: " + idKunjungan + " <<<");
        System.out.println("Tingkat Prioritas : " + getTeksPrioritas());
        System.out.println("Nomor Antrean     : #" + nomorAntrean);

        // Penerapan Polymorphism: Memanggil method beriPelayanan melalui referensi TenagaMedis
        tenagaMedis.beriPelayanan(pasien, keluhan);

        this.status = "SELESAI";
        System.out.println(">>> Status kunjungan [" + idKunjungan + "] berhasil diperbarui menjadi SELESAI <<<\n");
    }

    /**
     * FITUR KREATIF:
     * Logika pengurutan antrean:
     * 1. Prioritas terkecil (1: Darurat, 2: Mendesak, 3: Reguler) didahulukan.
     * 2. Jika prioritas sama, dahulukan nomor antrean yang lebih awal (FIFO).
     */
    @Override
    public int compareTo(Kunjungan other) {
        if (this.prioritas != other.prioritas) {
            return Integer.compare(this.prioritas, other.prioritas);
        }
        return Integer.compare(this.nomorAntrean, other.nomorAntrean);
    }

    /**
     * Menampilkan ringkasan kunjungan dalam format baris tabel.
     */
    public void tampilkanBarisTabel() {
        System.out.printf("| %-6s | #%-4d | %-12s | %-16s | %-16s | %-14s | %-9s |%n",
                idKunjungan,
                nomorAntrean,
                getTeksPrioritas(),
                pasien.getNama(),
                tenagaMedis.getNama(),
                keluhan,
                status);
    }
}
