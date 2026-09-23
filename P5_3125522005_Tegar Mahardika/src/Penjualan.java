public class Penjualan {
    private Pelanggan pelanggan;
    private Produk produk;
    private int jumlah;

    public Penjualan(Pelanggan pelanggan, Produk produk, int jumlah) {
        this.pelanggan = pelanggan;
        this.produk = produk;
        this.jumlah = jumlah;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public Produk getProduk() {
        return produk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void tampilkanTransaksi() {
        System.out.println("=== DATA TRANSAKSI ===");
        System.out.println("Pelanggan : " + pelanggan.getNama());
        System.out.println("Produk    : " + produk.getNamaProduk());
        System.out.println("Harga     : " + produk.getHarga());
        System.out.println("Jumlah    : " + jumlah);
    }
}