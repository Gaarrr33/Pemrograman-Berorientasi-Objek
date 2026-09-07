public class Main {
    public static void main(String[] args) {

        // =========================
        // OBJECT PRODUK
        // =========================

        Produk produk1 = new Produk(
            "P001",
            "Pulpen",
            3000
        );

        Produk produk2 = new Produk(
            "P002",
            "Buku Tulis",
            5000
        );

        System.out.println("===== DATA PRODUK 1 =====");
        produk1.tampilkanData();

        System.out.println("\n===== DATA PRODUK 2 =====");
        produk2.tampilkanData();


        // =========================
        // OBJECT PENJUALAN
        // =========================

        Penjualan transaksi1 = new Penjualan(
            "TRX001",
            produk1,
            2
        );

        Penjualan transaksi2 = new Penjualan(
            "TRX002",
            produk2,
            3
        );

        System.out.println("\n===== DATA TRANSAKSI 1 =====");
        transaksi1.tampilkanTransaksi();

        System.out.println("\n===== DATA TRANSAKSI 2 =====");
        transaksi2.tampilkanTransaksi();


        // =========================
        // OBJECT PELANGGAN
        // =========================

        Pelanggan pelanggan1 = new Pelanggan(
            "C001",
            "Tegar",
            "081234567890"
        );

        Pelanggan pelanggan2 = new Pelanggan(
            "C002",
            "Budi",
            "082345678901"
        );

        System.out.println("\n===== DATA PELANGGAN 1 =====");
        pelanggan1.tampilkanNama();

        System.out.println("\n===== DATA PELANGGAN 2 =====");
        pelanggan2.tampilkanNama();


        // =========================
        // PENGUJIAN METHOD PARAMETER
        // =========================

        produk1.ubahHarga(3500);
        transaksi1.ubahJumlah(4);
        pelanggan1.ubahNama("Tegar Mahardika");


        // =============oke, kasi ============
        // PENGUJIAN RETURN VALUE
        // =========================

        System.out.println("\n===== HASIL PERUBAHAN =====");

        System.out.println(
            "Harga baru " + produk1.nama +
            " : Rp" + produk1.getHarga()
        );

        System.out.println(
            "Total transaksi 1 : Rp" +
            transaksi1.hitungTotal()
        );

        System.out.println(
            "Nama pelanggan 1 : " +
            pelanggan1.getNama()
        );
    }
}