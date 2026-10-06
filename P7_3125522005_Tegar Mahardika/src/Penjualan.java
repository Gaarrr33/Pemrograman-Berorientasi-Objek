public class Penjualan {
    private Pelanggan pelanggan;
    private Produk produk;
    private int jumlah;
    private DetailPenjualan detailPenjualan;

    public Penjualan(
            Pelanggan pelanggan,
            Produk produk,
            int jumlah
    ) {
        this.pelanggan = pelanggan;
        this.produk = produk;
        this.jumlah = jumlah;

        // Composition dari Penjualan ke DetailPenjualan
        this.detailPenjualan =
                new DetailPenjualan(produk, jumlah);
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

    public DetailPenjualan getDetailPenjualan() {
        return detailPenjualan;
    }

    // Method Overloading
    public double hitungTotal(int jumlah) {
        return produk.getHarga() * jumlah;
    }

    // Method Overloading
    public double hitungTotal(
            int jumlah,
            double diskon
    ) {
        double total = produk.getHarga() * jumlah;

        return total - diskon;
    }

    public void tampilkanTransaksi() {
        System.out.println("=== DATA TRANSAKSI ===");
        System.out.println(
                "Pelanggan : " + pelanggan.getNama()
        );
        System.out.println(
                "Produk    : " + produk.getNamaProduk()
        );
        System.out.println(
                "Harga     : Rp" + produk.getHarga()
        );
        System.out.println(
                "Jumlah    : " + jumlah
        );
        System.out.println(
                "Total     : Rp" + hitungTotal(jumlah)
        );
    }
}