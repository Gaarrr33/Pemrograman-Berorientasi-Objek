public class ProdukFotokopi extends Produk {
    private String jenisKertas;

    public ProdukFotokopi(String namaProduk, double harga, int stok, String jenisKertas) {
        super(namaProduk, harga, stok);
        this.jenisKertas = jenisKertas;

        System.out.println("Constructor ProdukFotokopi dijalankan");
    }

    public String getJenisKertas() {
        return jenisKertas;
    }
}