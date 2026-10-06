public class ProdukATK extends Produk implements DapatDiskon {
    private String jenisATK;

    public ProdukATK(String namaProduk, double harga, int stok, String jenisATK) {
        super(namaProduk, harga, stok);
        this.jenisATK = jenisATK;
    }

    public String getJenisATK() {
        return jenisATK;
    }

    // Implementasi Abstract Method dari Produk
    @Override
    public void tampilkanInfo() {
        System.out.println("=== PRODUK ATK ===");
        System.out.println("Nama Produk : " + getNamaProduk());
        System.out.println("Harga       : Rp" + getHarga());
        System.out.println("Stok        : " + getStok());
        System.out.println("Jenis ATK   : " + jenisATK);
    }

    // Implementasi Method dari Interface DapatDiskon
    @Override
    public double hitungHargaDiskon(double persentase) {
        return getHarga() - (getHarga() * (persentase / 100.0));
    }
}