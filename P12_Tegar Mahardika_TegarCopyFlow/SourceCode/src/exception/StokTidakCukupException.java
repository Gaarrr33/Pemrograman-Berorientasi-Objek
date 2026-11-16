package exception;

/**
 * Custom Checked Exception: StokTidakCukupException
 * Dilempar ketika kuantitas barang dagangan fisik (PRODUCT) yang diminta
 * melampaui total stok fisik yang tersedia pada akumulasi batch FIFO aktif.
 */
public class StokTidakCukupException extends Exception {
    private final String namaProduk;
    private final int stokTersedia;
    private final int jumlahDiminta;
    private final String satuan;

    public StokTidakCukupException(String namaProduk, int stokTersedia, int jumlahDiminta, String satuan) {
        super(String.format("Stok '%s' tidak mencukupi! Tersedia: %d %s, Diminta: %d %s.",
                namaProduk, stokTersedia, satuan, jumlahDiminta, satuan));
        this.namaProduk = namaProduk;
        this.stokTersedia = stokTersedia;
        this.jumlahDiminta = jumlahDiminta;
        this.satuan = satuan;
    }

    public StokTidakCukupException(String pesan) {
        super(pesan);
        this.namaProduk = "-";
        this.stokTersedia = 0;
        this.jumlahDiminta = 0;
        this.satuan = "unit";
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public int getStokTersedia() {
        return stokTersedia;
    }

    public int getJumlahDiminta() {
        return jumlahDiminta;
    }

    public String getSatuan() {
        return satuan;
    }
}

