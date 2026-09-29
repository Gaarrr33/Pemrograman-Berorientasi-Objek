public class ProdukFotokopi extends Produk {
    private String jenisKertas;

    public ProdukFotokopi(
            String namaProduk,
            double harga,
            int stok,
            String jenisKertas
    ) {
        super(namaProduk, harga, stok);
        this.jenisKertas = jenisKertas;
    }

    public String getJenisKertas() {
        return jenisKertas;
    }

    // Method Overriding
    @Override
    public void tampilkanInfo() {
        System.out.println("=== PRODUK FOTOKOPI ===");
        System.out.println("Nama Produk: " + getNamaProduk());
        System.out.println("Harga: Rp" + getHarga());
        System.out.println("Stok: " + getStok());
        System.out.println("Jenis Kertas: " + jenisKertas);
    }
}