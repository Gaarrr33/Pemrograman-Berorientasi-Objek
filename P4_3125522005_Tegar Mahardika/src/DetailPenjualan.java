public class DetailPenjualan {

    private Produk produk;
    private int jumlah;

    public DetailPenjualan(Produk produk, int jumlah) {

        if (produk == null) {
            throw new IllegalArgumentException(
                "Produk tidak boleh null."
            );
        }

        if (jumlah <= 0) {
            throw new IllegalArgumentException(
                "Jumlah harus lebih dari 0."
            );
        }

        this.produk = produk;
        this.jumlah = jumlah;
    }

    public Produk getProduk() {
        return produk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public double hitungSubtotal() {
        return produk.getHarga() * jumlah;
    }

    public void tampilkanDetail() {
        System.out.println("Produk   : " + produk.getNama());
        System.out.println("Harga    : Rp" + produk.getHarga());
        System.out.println("Jumlah   : " + jumlah);
        System.out.println("Subtotal : Rp" + hitungSubtotal());
    }
}