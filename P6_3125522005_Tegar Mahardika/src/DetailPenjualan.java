public class DetailPenjualan {
    private Produk produk;
    private int jumlah;

    public DetailPenjualan(Produk produk, int jumlah) {
        this.produk = produk;
        this.jumlah = jumlah;
    }

    public Produk getProduk() {
        return produk;
    }

    public int getJumlah() {
        return jumlah;
    }
}