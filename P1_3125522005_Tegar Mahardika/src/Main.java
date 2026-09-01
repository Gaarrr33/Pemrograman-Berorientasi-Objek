public class Main {
    public static void main(String[] args) {

        Produk produk1 = new Produk();

        produk1.kode = "P001";
        produk1.nama = "Pulpen";
        produk1.harga = 3000;

        System.out.println("=== DATA PRODUK ===");
        produk1.tampilkanData();

        Penjualan transaksi1 = new Penjualan();

        transaksi1.kodeTransaksi = "TRX001";
        transaksi1.produk = produk1;
        transaksi1.jumlah = 2;

        System.out.println();
        System.out.println("=== DATA TRANSAKSI ===");
        transaksi1.tampilkanTransaksi();
    }
}