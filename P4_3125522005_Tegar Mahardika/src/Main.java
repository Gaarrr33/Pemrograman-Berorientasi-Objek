public class Main {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println(" SISTEM PENJUALAN FOTOKOPI & ATK");
        System.out.println("=================================");
        System.out.println();

        // Object Produk
        Produk produk = new Produk(
            "P001",
            "Kertas A4",
            500
        );

        // Object Pelanggan
        Pelanggan pelanggan = new Pelanggan(
            "C001",
            "Tegar",
            "081234567890"
        );

        // Object Penjualan
        Penjualan penjualan = new Penjualan(
            "TRX001",
            pelanggan,
            produk,
            10
        );

        System.out.println("--- DATA PRODUK ---");
        produk.tampilkanData();

        System.out.println();

        System.out.println("--- DATA PELANGGAN ---");
        pelanggan.tampilkanNama();

        System.out.println();

        System.out.println("--- DATA TRANSAKSI ---");
        penjualan.tampilkanTransaksi();

        System.out.println();

        System.out.println("--- DETAIL PENJUALAN ---");
        penjualan.getDetail().tampilkanDetail();

        System.out.println();

        System.out.println("--- UBAH JUMLAH ---");
        penjualan.ubahJumlah(20);
        penjualan.tampilkanTransaksi();

        System.out.println();

        System.out.println("Program selesai.");
    }
}