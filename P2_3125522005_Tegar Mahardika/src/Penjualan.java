public class Penjualan {
    String kodeTransaksi;
    Produk produk;
    int jumlah;

    public Penjualan(String kodeTransaksi, Produk produk, int jumlah) {
        this.kodeTransaksi = kodeTransaksi;
        this.produk = produk;
        this.jumlah = jumlah;
    }

    // Method dengan return value
    public double hitungTotal() {
        return produk.getHarga() * jumlah;
    }

    // Method tanpa parameter
    public void tampilkanTransaksi() {
        System.out.println("Kode Transaksi : " + kodeTransaksi);
        System.out.println("Produk         : " + produk.nama);
        System.out.println("Jumlah         : " + jumlah);
        System.out.println("Total          : Rp" + hitungTotal());
    }

    // Method dengan parameter
    public void ubahJumlah(int jumlahBaru) {
        jumlah = jumlahBaru;
    }
}