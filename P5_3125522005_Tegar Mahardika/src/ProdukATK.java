public class ProdukATK extends Produk {
    private String jenisATK;

    public ProdukATK(String namaProduk, double harga, int stok, String jenisATK) {
        super(namaProduk, harga, stok);
        this.jenisATK = jenisATK;

        System.out.println("Constructor ProdukATK dijalankan");
    }

    public String getJenisATK() {
        return jenisATK;
    }
}