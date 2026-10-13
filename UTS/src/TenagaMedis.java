/**
 * Abstract Class TenagaMedis merepresentasikan tenaga medis umum di klinik.
 * Mengimplementasikan interface PelayananMedis dan menjadi superclass
 * bagi Dokter dan Perawat.
 */
public abstract class TenagaMedis implements PelayananMedis {
    private String idTenagaMedis;
    private String nama;
    private String spesialisasi;

    /**
     * Constructor untuk inisialisasi TenagaMedis.
     * @param idTenagaMedis ID unik tenaga medis
     * @param nama Nama lengkap beserta gelar
     * @param spesialisasi Bidang keahlian/spesialisasi
     */
    public TenagaMedis(String idTenagaMedis, String nama, String spesialisasi) {
        if (idTenagaMedis == null || idTenagaMedis.trim().isEmpty()) {
            throw new IllegalArgumentException("ID Tenaga Medis tidak boleh kosong!");
        }
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama Tenaga Medis tidak boleh kosong!");
        }
        this.idTenagaMedis = idTenagaMedis.trim();
        this.nama = nama.trim();
        this.spesialisasi = (spesialisasi != null) ? spesialisasi.trim() : "Umum";
    }

    // --- Getter dan Setter (Encapsulation) ---

    public String getIdTenagaMedis() {
        return idTenagaMedis;
    }

    public void setIdTenagaMedis(String idTenagaMedis) {
        this.idTenagaMedis = idTenagaMedis;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getSpesialisasi() {
        return spesialisasi;
    }

    public void setSpesialisasi(String spesialisasi) {
        this.spesialisasi = spesialisasi;
    }

    /**
     * Abstract Method yang wajib diimplementasikan oleh setiap subclass (Dokter & Perawat).
     * Menerapkan prinsip Polymorphism.
     */
    @Override
    public abstract void beriPelayanan(Pasien pasien, String keluhan);

    /**
     * Menampilkan profil ringkas tenaga medis.
     */
    public void tampilkanProfil() {
        System.out.printf("[ID: %-6s] %-25s | Peran: %-22s | Bidang: %s%n",
                idTenagaMedis, nama, getPeran(), spesialisasi);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", idTenagaMedis, nama, getPeran());
    }
}
