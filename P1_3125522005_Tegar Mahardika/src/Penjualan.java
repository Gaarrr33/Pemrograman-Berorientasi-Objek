public class Penjualan {
    String kodeTransaksi;
    Produk produk;
    int jumlah;

    double hitungTotal() {
        return produk.harga * jumlah;
    }

    void tampilkanTransaksi() {
        System.out.println("Kode Transaksi : " + kodeTransaksi);
        System.out.println("Produk         : " + produk.nama);
        System.out.println("Jumlah         : " + jumlah);
        System.out.println("Total          : Rp" + hitungTotal());
    }
}