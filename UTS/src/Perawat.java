/**
 * Subclass Perawat mewarisi abstract class TenagaMedis.
 * Memiliki atribut spesifik shift kerja dan ruang tugas, serta
 * mengimplementasikan logika pelayanan khas asuhan keperawatan.
 */
public class Perawat extends TenagaMedis {
    private String shiftKerja; // Pagi, Siang, Malam
    private String ruangTugas;  // IGD, Poli Umum, Ruang Tindakan

    /**
     * Constructor untuk membuat objek Perawat.
     * @param idTenagaMedis ID unik tenaga medis
     * @param nama Nama lengkap perawat
     * @param spesialisasi Keahlian keperawatan
     * @param shiftKerja Shift kerja (Pagi / Siang / Malam)
     * @param ruangTugas Ruang tempat bertugas
     */
    public Perawat(String idTenagaMedis, String nama, String spesialisasi, String shiftKerja, String ruangTugas) {
        super(idTenagaMedis, nama, spesialisasi);
        this.shiftKerja = (shiftKerja != null && !shiftKerja.trim().isEmpty()) ? shiftKerja : "Pagi";
        this.ruangTugas = (ruangTugas != null && !ruangTugas.trim().isEmpty()) ? ruangTugas : "Poli Umum";
    }

    public String getShiftKerja() {
        return shiftKerja;
    }

    public void setShiftKerja(String shiftKerja) {
        this.shiftKerja = shiftKerja;
    }

    public String getRuangTugas() {
        return ruangTugas;
    }

    public void setRuangTugas(String ruangTugas) {
        this.ruangTugas = ruangTugas;
    }

    @Override
    public String getPeran() {
        return "Perawat (" + ruangTugas + ")";
    }

    /**
     * Implementasi konkret dari abstract method beriPelayanan (Polymorphism).
     * Perawat berfokus pada triase, pengukuran tanda-tanda vital, dan perawatan pendukung.
     */
    @Override
    public void beriPelayanan(Pasien pasien, String keluhan) {
        System.out.println("  -------------------------------------------------------------");
        System.out.println("  [TINDAKAN ASUHAN KEPERAWATAN]");
        System.out.println("  Perawat Bertugas : " + getNama() + " (Shift: " + shiftKerja + ")");
        System.out.println("  Ruang Pelayanan  : " + ruangTugas);
        System.out.println("  Pasien           : " + pasien.getNama() + " (" + pasien.getUsia() + " tahun)");
        System.out.println("  Keluhan Masuk    : " + keluhan);
        System.out.println("  Tindakan Asuhan  : 1. Melakukan triase awal & observasi pasien.");
        System.out.println("                     2. Memeriksa tanda vital (Tensi, Nadi, Suhu tubuh).");
        System.out.println("                     3. Memberikan pertolongan pertama / stabilisasi kondisi.");
        System.out.println("  Status Tindakan  : Pasien telah distabilkan dan siap diobservasi.");
        System.out.println("  -------------------------------------------------------------");
    }
}
