/**
 * Subclass Dokter mewarisi abstract class TenagaMedis.
 * Memiliki atribut spesifik nomor SIP dan biaya konsultasi, serta
 * mengimplementasikan logika pelayanan khas dokter.
 */
public class Dokter extends TenagaMedis {
    private String nomorSip;
    private double biayaKonsultasi;

    /**
     * Constructor untuk membuat objek Dokter.
     * @param idTenagaMedis ID unik tenaga medis
     * @param nama Nama lengkap dokter
     * @param spesialisasi Spesialisasi medis (misal: Umum, Anak, Penyakit Dalam)
     * @param nomorSip Surat Izin Praktik (SIP)
     * @param biayaKonsultasi Biaya jasa konsultasi dokter
     */
    public Dokter(String idTenagaMedis, String nama, String spesialisasi, String nomorSip, double biayaKonsultasi) {
        super(idTenagaMedis, nama, spesialisasi);
        this.nomorSip = nomorSip;
        this.biayaKonsultasi = (biayaKonsultasi >= 0) ? biayaKonsultasi : 0.0;
    }

    public String getNomorSip() {
        return nomorSip;
    }

    public void setNomorSip(String nomorSip) {
        this.nomorSip = nomorSip;
    }

    public double getBiayaKonsultasi() {
        return biayaKonsultasi;
    }

    public void setBiayaKonsultasi(double biayaKonsultasi) {
        if (biayaKonsultasi < 0) {
            throw new IllegalArgumentException("Biaya konsultasi tidak boleh negatif!");
        }
        this.biayaKonsultasi = biayaKonsultasi;
    }

    @Override
    public String getPeran() {
        return "Dokter " + getSpesialisasi();
    }

    /**
     * Implementasi konkret dari abstract method beriPelayanan (Polymorphism).
     * Dokter berfokus pada anamnesis mendalam, perumusan diagnosis medis, dan peresepan obat.
     */
    @Override
    public void beriPelayanan(Pasien pasien, String keluhan) {
        System.out.println("  -------------------------------------------------------------");
        System.out.println("  [TINDAKAN MEDIS DOKTER]");
        System.out.println("  Dokter Pemeriksa : " + getNama() + " (SIP: " + nomorSip + ")");
        System.out.println("  Pasien           : " + pasien.getNama() + " (" + pasien.getUsia() + " tahun)");
        System.out.println("  Keluhan Utama    : " + keluhan);
        System.out.println("  Tindakan Medis   : 1. Melakukan pemeriksaan fisik & diagnosis klinis.");
        System.out.println("                     2. Menetapkan terapi dan menulis resep obat.");
        System.out.printf("  Biaya Konsultasi : Rp %,.2f%n", biayaKonsultasi);
        System.out.println("  Status Tindakan  : Pemeriksaan dokter selesai, resep diserahkan.");
        System.out.println("  -------------------------------------------------------------");
    }
}
