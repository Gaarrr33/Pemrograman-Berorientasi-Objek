/**
 * Interface PelayananMedis mendefinisikan kontrak pelayanan yang harus
 * diimplementasikan oleh setiap tenaga medis di klinik.
 */
public interface PelayananMedis {
    /**
     * Memberikan pelayanan medis kepada pasien berdasarkan keluhan.
     * @param pasien Objek pasien yang menerima pelayanan
     * @param keluhan Keluhan sakit yang dialami pasien
     */
    void beriPelayanan(Pasien pasien, String keluhan);

    /**
     * Mengembalikan peran atau gelar profesi tenaga medis.
     * @return Peran profesi tenaga medis
     */
    String getPeran();
}
