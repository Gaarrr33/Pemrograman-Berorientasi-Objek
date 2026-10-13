/**
 * Class Pasien merepresentasikan entitas pasien yang berobat di klinik.
 * Menerapkan prinsip Encapsulation dengan atribut private dan validasi data.
 */
public class Pasien {
    private String idPasien;
    private String nama;
    private int usia;
    private String jenisKelamin;
    private String alamat;

    /**
     * Constructor untuk inisialisasi objek Pasien beserta validasinya.
     * @param idPasien ID unik pasien
     * @param nama Nama lengkap pasien
     * @param usia Usia pasien dalam tahun
     * @param jenisKelamin Jenis kelamin pasien (L/P)
     * @param alamat Alamat tempat tinggal pasien
     */
    public Pasien(String idPasien, String nama, int usia, String jenisKelamin, String alamat) {
        setIdPasien(idPasien);
        setNama(nama);
        setUsia(usia);
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
    }

    // --- Getter dan Setter dengan Validasi (Encapsulation) ---

    public String getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(String idPasien) {
        if (idPasien == null || idPasien.trim().isEmpty()) {
            throw new IllegalArgumentException("ID Pasien tidak boleh kosong!");
        }
        this.idPasien = idPasien.trim();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        // Business Rule: Nama pasien tidak boleh kosong
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama pasien tidak boleh kosong!");
        }
        this.nama = nama.trim();
    }

    public int getUsia() {
        return usia;
    }

    public void setUsia(int usia) {
        // Business Rule: Umur pasien tidak boleh negatif atau 0
        if (usia <= 0) {
            throw new IllegalArgumentException("Usia pasien harus lebih besar dari 0 tahun!");
        }
        this.usia = usia;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    /**
     * Menampilkan informasi ringkas pasien.
     */
    public void tampilkanInfo() {
        System.out.printf("[ID: %-5s] %-18s | %2d th | %-9s | %s%n",
                idPasien, nama, usia, jenisKelamin, alamat);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d th, %s)", idPasien, nama, usia, jenisKelamin);
    }
}
