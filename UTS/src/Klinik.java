import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Klinik bertindak sebagai pengelola utama sistem klinik.
 * Menerapkan relasi agregasi/komposisi terhadap Pasien, TenagaMedis, dan Kunjungan.
 */
public class Klinik {
    private String namaKlinik;
    private String alamat;
    private List<Pasien> daftarPasien;
    private List<TenagaMedis> daftarTenagaMedis;
    private List<Kunjungan> daftarKunjungan;
    private int counterAntrean;
    private int counterKunjungan;

    /**
     * Inisialisasi sistem klinik.
     * @param namaKlinik Nama klinik
     * @param alamat Alamat klinik
     */
    public Klinik(String namaKlinik, String alamat) {
        this.namaKlinik = namaKlinik;
        this.alamat = alamat;
        this.daftarPasien = new ArrayList<>();
        this.daftarTenagaMedis = new ArrayList<>();
        this.daftarKunjungan = new ArrayList<>();
        this.counterAntrean = 1;
        this.counterKunjungan = 1;
    }

    public String getNamaKlinik() {
        return namaKlinik;
    }

    public String getAlamat() {
        return alamat;
    }

    public List<Pasien> getDaftarPasien() {
        return daftarPasien;
    }

    public List<TenagaMedis> getDaftarTenagaMedis() {
        return daftarTenagaMedis;
    }

    public List<Kunjungan> getDaftarKunjungan() {
        return daftarKunjungan;
    }

    // =========================================================================
    // BUSINESS RULES & MANAJEMEN DATA PASIEN
    // =========================================================================

    /**
     * Mendaftarkan pasien baru ke klinik.
     * Business Rule: Pasien dengan ID yang sama tidak boleh didaftarkan dua kali.
     * @param pasien Objek pasien yang akan didaftarkan
     */
    public void tambahPasien(Pasien pasien) {
        if (pasien == null) {
            throw new IllegalArgumentException("Data pasien tidak boleh null!");
        }

        // Cek duplikasi ID Pasien
        for (Pasien p : daftarPasien) {
            if (p.getIdPasien().equalsIgnoreCase(pasien.getIdPasien())) {
                throw new IllegalArgumentException("Pelanggaran Aturan Bisnis: Pasien dengan ID [" +
                        pasien.getIdPasien() + "] sudah terdaftar di klinik!");
            }
        }

        daftarPasien.add(pasien);
    }

    /**
     * Mencari data pasien berdasarkan ID.
     */
    public Pasien cariPasien(String idPasien) {
        if (idPasien == null) return null;
        for (Pasien p : daftarPasien) {
            if (p.getIdPasien().equalsIgnoreCase(idPasien.trim())) {
                return p;
            }
        }
        return null;
    }

    // =========================================================================
    // MANAJEMEN TENAGA MEDIS
    // =========================================================================

    /**
     * Menambahkan tenaga medis (Dokter / Perawat) ke klinik.
     */
    public void tambahTenagaMedis(TenagaMedis tenagaMedis) {
        if (tenagaMedis == null) {
            throw new IllegalArgumentException("Data tenaga medis tidak boleh null!");
        }
        for (TenagaMedis tm : daftarTenagaMedis) {
            if (tm.getIdTenagaMedis().equalsIgnoreCase(tenagaMedis.getIdTenagaMedis())) {
                throw new IllegalArgumentException("Tenaga medis dengan ID [" +
                        tenagaMedis.getIdTenagaMedis() + "] sudah terdaftar!");
            }
        }
        daftarTenagaMedis.add(tenagaMedis);
    }

    /**
     * Mencari tenaga medis berdasarkan ID.
     */
    public TenagaMedis cariTenagaMedis(String idTenagaMedis) {
        if (idTenagaMedis == null) return null;
        for (TenagaMedis tm : daftarTenagaMedis) {
            if (tm.getIdTenagaMedis().equalsIgnoreCase(idTenagaMedis.trim())) {
                return tm;
            }
        }
        return null;
    }

    // =========================================================================
    // FITUR KREATIF & MANAJEMEN KUNJUNGAN PASIEN
    // =========================================================================

    /**
     * Mendaftarkan kunjungan pasien ke klinik.
     * @param idPasien ID Pasien
     * @param idTenagaMedis ID Tenaga Medis pemeriksa
     * @param keluhan Keluhan sakit
     * @param prioritas Tingkat urgensi: 1=Darurat, 2=Mendesak, 3=Reguler
     * @return Objek Kunjungan yang berhasil dibuat
     */
    public Kunjungan daftarkanKunjungan(String idPasien, String idTenagaMedis, String keluhan, int prioritas) {
        Pasien pasien = cariPasien(idPasien);
        if (pasien == null) {
            throw new IllegalArgumentException("Gagal mendaftar: Pasien dengan ID [" + idPasien + "] tidak ditemukan!");
        }

        TenagaMedis tenagaMedis = cariTenagaMedis(idTenagaMedis);
        if (tenagaMedis == null) {
            throw new IllegalArgumentException("Gagal mendaftar: Tenaga medis dengan ID [" + idTenagaMedis + "] tidak ditemukan!");
        }

        String idKunjungan = String.format("KJ%03d", counterKunjungan++);
        int nomorAntrean = counterAntrean++;

        Kunjungan kunjungan = new Kunjungan(idKunjungan, nomorAntrean, pasien, tenagaMedis, keluhan, prioritas);
        daftarKunjungan.add(kunjungan);
        return kunjungan;
    }

    /**
     * Mengambil daftar antrean aktif (status MENUNGGU) yang telah diurutkan
     * berdasarkan FITUR KREATIF:
     * - Tingkat prioritas (1: Darurat, 2: Mendesak, 3: Reguler)
     * - Nomor antrean pendaftaran (FIFO jika prioritas sama).
     */
    public List<Kunjungan> getAntreanMenungguTerurut() {
        List<Kunjungan> antrean = new ArrayList<>();
        for (Kunjungan k : daftarKunjungan) {
            if ("MENUNGGU".equalsIgnoreCase(k.getStatus())) {
                antrean.add(k);
            }
        }
        Collections.sort(antrean);
        return antrean;
    }

    /**
     * Memproses pelayanan untuk pasien pada antrean terdepan
     * (prioritas tertinggi yang menunggu).
     */
    public Kunjungan prosesAntreanTerdepan() {
        List<Kunjungan> antrean = getAntreanMenungguTerurut();
        if (antrean.isEmpty()) {
            System.out.println("[INFO] Tidak ada pasien dalam antrean yang menunggu.");
            return null;
        }

        Kunjungan antreanTerdepan = antrean.get(0);
        antreanTerdepan.prosesPelayanan();
        return antreanTerdepan;
    }

    /**
     * Mencari kunjungan berdasarkan ID Kunjungan.
     */
    public Kunjungan cariKunjungan(String idKunjungan) {
        if (idKunjungan == null) return null;
        for (Kunjungan k : daftarKunjungan) {
            if (k.getIdKunjungan().equalsIgnoreCase(idKunjungan.trim())) {
                return k;
            }
        }
        return null;
    }

    // =========================================================================
    // METODE TAMPILAN CONSOLE
    // =========================================================================

    public void tampilkanDaftarPasien() {
        System.out.println("\n====================== DAFTAR PASIEN KLINIK ======================");
        if (daftarPasien.isEmpty()) {
            System.out.println("Belum ada pasien terdaftar.");
            return;
        }
        for (Pasien p : daftarPasien) {
            p.tampilkanInfo();
        }
        System.out.println("Total: " + daftarPasien.size() + " pasien.");
    }

    public void tampilkanDaftarTenagaMedis() {
        System.out.println("\n=================== DAFTAR TENAGA MEDIS KLINIK ===================");
        if (daftarTenagaMedis.isEmpty()) {
            System.out.println("Belum ada tenaga medis terdaftar.");
            return;
        }
        for (TenagaMedis tm : daftarTenagaMedis) {
            tm.tampilkanProfil();
        }
        System.out.println("Total: " + daftarTenagaMedis.size() + " tenaga medis.");
    }

    public void tampilkanAntreanPrioritas() {
        System.out.println("\n================ ANTREAN AKTIF BERDASARKAN PRIORITAS ================");
        List<Kunjungan> antrean = getAntreanMenungguTerurut();
        if (antrean.isEmpty()) {
            System.out.println("Tidak ada pasien dalam antrean (semua sudah dilayani).");
            return;
        }
        System.out.println("+--------+-------+--------------+------------------+------------------+----------------+-----------+");
        System.out.println("| ID     | Antre | Prioritas    | Nama Pasien      | Tenaga Medis     | Keluhan        | Status    |");
        System.out.println("+--------+-------+--------------+------------------+------------------+----------------+-----------+");
        for (Kunjungan k : antrean) {
            k.tampilkanBarisTabel();
        }
        System.out.println("+--------+-------+--------------+------------------+------------------+----------------+-----------+");
        System.out.println("* Keterangan: Prioritas 1: Darurat > Prioritas 2: Mendesak > Prioritas 3: Reguler");
    }

    public void tampilkanSemuaKunjungan() {
        System.out.println("\n==================== RIWAYAT SEMUA KUNJUNGAN ====================");
        if (daftarKunjungan.isEmpty()) {
            System.out.println("Belum ada catatan kunjungan.");
            return;
        }
        System.out.println("+--------+-------+--------------+------------------+------------------+----------------+-----------+");
        System.out.println("| ID     | Antre | Prioritas    | Nama Pasien      | Tenaga Medis     | Keluhan        | Status    |");
        System.out.println("+--------+-------+--------------+------------------+------------------+----------------+-----------+");
        for (Kunjungan k : daftarKunjungan) {
            k.tampilkanBarisTabel();
        }
        System.out.println("+--------+-------+--------------+------------------+------------------+----------------+-----------+");
    }

    /**
     * Mengisi data awal (seed data) agar pengujian dapat langsung dilakukan.
     */
    public void inisialisasiDataAwal() {
        // Tambah Tenaga Medis (Dokter & Perawat)
        tambahTenagaMedis(new Dokter("DOC01", "dr. Budi Santoso, Sp.PD", "Penyakit Dalam", "SIP-12345/IDI", 150000));
        tambahTenagaMedis(new Dokter("DOC02", "dr. Siti Rahma, Sp.A", "Anak", "SIP-67890/IDI", 175000));
        tambahTenagaMedis(new Perawat("NRS01", "Ners Andi Pratama, S.Kep", "Keperawatan Gawat Darurat", "Pagi", "IGD"));
        tambahTenagaMedis(new Perawat("NRS02", "Ners Maya Safitri, A.Md.Kep", "Keperawatan Umum", "Siang", "Poli Umum"));

        // Tambah Pasien Awal
        tambahPasien(new Pasien("P001", "Ahmad Fauzi", 45, "Laki-laki", "Jl. Merdeka No. 10"));
        tambahPasien(new Pasien("P002", "Dewi Sartika", 28, "Perempuan", "Jl. Melati No. 5"));
        tambahPasien(new Pasien("P003", "Bambang Pamungkas", 60, "Laki-laki", "Jl. Sudirman No. 12"));
        tambahPasien(new Pasien("P004", "Clara Amanda", 7, "Perempuan", "Jl. Kenanga No. 8"));

        // Daftarkan Contoh Kunjungan Awal dengan Urutan Acak untuk menguji Fitur Kreatif
        // P001 daftar lebih dulu (Antrean #1) tapi prioritas 3 (Reguler)
        daftarkanKunjungan("P001", "DOC01", "Kontrol Maag Rutin", 3);

        // P002 daftar kedua (Antrean #2) dengan prioritas 2 (Mendesak)
        daftarkanKunjungan("P002", "NRS02", "Demam Tinggi 3 Hari", 2);

        // P003 daftar ketiga (Antrean #3) dengan prioritas 1 (Darurat - Serangan Jantung/Sesak)
        daftarkanKunjungan("P003", "NRS01", "Nyeri Dada Akut & Sesak", 1);

        // P004 daftar keempat (Antrean #4) dengan prioritas 2 (Mendesak)
        daftarkanKunjungan("P004", "DOC02", "Batuk Rejan & Diare", 2);
    }
}
