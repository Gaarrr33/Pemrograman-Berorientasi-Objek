public class Main {
    public static void main(String[] args) {

        // ==================================================
        // TEST VALID
        // ==================================================

        System.out.println("======================================");
        System.out.println("           TEST VALID");
        System.out.println("======================================");

        Produk produk1 = new Produk(
            "P001",
            "Kertas A4",
            500
        );

        Produk produk2 = new Produk(
            "P002",
            "Pulpen",
            3000
        );

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

        Penjualan penjualan1 = new Penjualan(
            "TRX001",
            produk1,
            100
        );

        Penjualan penjualan2 = new Penjualan(
            "TRX002",
            produk2,
            5
        );

        System.out.println("\n--- DATA PRODUK ---");

        produk1.tampilkanData();
        System.out.println();

        produk2.tampilkanData();

        System.out.println("\n--- DATA PELANGGAN ---");

        pelanggan1.tampilkanNama();
        System.out.println("No. Telepon: "
                + pelanggan1.getNoTelepon());

        System.out.println();

        pelanggan2.tampilkanNama();
        System.out.println("No. Telepon: "
                + pelanggan2.getNoTelepon());

        System.out.println("\n--- DATA PENJUALAN ---");

        penjualan1.tampilkanTransaksi();

        System.out.println();

        penjualan2.tampilkanTransaksi();


        // ==================================================
        // TEST SETTER VALID
        // ==================================================

        System.out.println("\n======================================");
        System.out.println("        TEST SETTER VALID");
        System.out.println("======================================");

        produk1.ubahHarga(600);

        System.out.println(
            "Harga produk setelah diubah: Rp"
            + produk1.getHarga()
        );

        pelanggan1.ubahNama("Tegar Mahardika");

        System.out.println(
            "Nama pelanggan setelah diubah: "
            + pelanggan1.getNama()
        );

        penjualan1.ubahJumlah(150);

        System.out.println(
            "Jumlah penjualan setelah diubah: "
            + penjualan1.getJumlah()
        );


        // ==================================================
        // TEST INVALID
        // ==================================================

        System.out.println("\n======================================");
        System.out.println("          TEST INVALID");
        System.out.println("======================================");

        // Test 1
        System.out.println("\nTest 1 - Harga produk negatif:");

        produk1.setHarga(-500);

        System.out.println(
            "Harga setelah input invalid: Rp"
            + produk1.getHarga()
        );


        // Test 2
        System.out.println("\nTest 2 - Nama produk kosong:");

        produk1.setNama("");

        System.out.println(
            "Nama setelah input invalid: "
            + produk1.getNama()
        );


        // Test 3
        System.out.println("\nTest 3 - Jumlah penjualan nol:");

        penjualan1.setJumlah(0);

        System.out.println(
            "Jumlah setelah input invalid: "
            + penjualan1.getJumlah()
        );


        // Test 4
        System.out.println("\nTest 4 - Nama pelanggan kosong:");

        pelanggan1.setNama("");

        System.out.println(
            "Nama setelah input invalid: "
            + pelanggan1.getNama()
        );


        // Test 5
        System.out.println("\nTest 5 - Nomor telepon tidak valid:");

        pelanggan1.setNoTelepon("abc123");

        System.out.println(
            "No. Telepon setelah input invalid: "
            + pelanggan1.getNoTelepon()
        );


        // ==================================================
        // TEST METHOD
        // ==================================================

        System.out.println("\n======================================");
        System.out.println("           TEST METHOD");
        System.out.println("======================================");

        produk1.ubahHarga(700);

        pelanggan1.ubahNama("Tegar");

        penjualan1.ubahJumlah(200);

        System.out.println(
            "\nTotal penjualan setelah perubahan: Rp"
            + penjualan1.hitungTotal()
        );

        System.out.println("\n--- Transaksi Terakhir ---");

        penjualan1.tampilkanTransaksi();
    }
}