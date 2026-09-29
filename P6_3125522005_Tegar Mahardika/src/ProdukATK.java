public class ProdukATK extends Produk {
    private String jenisATK;

    public ProdukATK(String namaProduk, double harga, int stok, String jenisATK) {
        super(namaProduk, harga, stok);
        this.jenisATK = jenisATK;
    }

    public String getJenisATK() {
        return jenisATK;
    }

    // Method Overriding
    @Override
    public void tampilkanInfo() {
        System.out.println("=== PRODUK ATK ===");
        System.out.println("Nama Produk: " + getNamaProduk());
        System.out.println("Harga: Rp" + getHarga());
        System.out.println("Stok: " + getStok());
        System.out.println("Jenis ATK: " + jenisATK);
    }
}