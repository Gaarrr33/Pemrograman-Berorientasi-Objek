public class Main {
    public static void main(String[] args) {

        System.out.println("=== TEST INHERITANCE ===");

        ProdukATK atk = new ProdukATK(
                "Buku Tulis",
                5000,
                20,
                "Alat Tulis"
        );

        System.out.println();

        ProdukFotokopi fotokopi = new ProdukFotokopi(
                "Kertas A4",
                500,
                100,
                "A4"
        );

        System.out.println();

        System.out.println("=== DATA PRODUK ATK ===");
        System.out.println("Nama Produk : " + atk.getNamaProduk());
        System.out.println("Harga       : " + atk.getHarga());
        System.out.println("Stok        : " + atk.getStok());
        System.out.println("Jenis ATK   : " + atk.getJenisATK());

        System.out.println();

        System.out.println("=== DATA PRODUK FOTOKOPI ===");
        System.out.println("Nama Produk : " + fotokopi.getNamaProduk());
        System.out.println("Harga       : " + fotokopi.getHarga());
        System.out.println("Stok        : " + fotokopi.getStok());
        System.out.println("Jenis Kertas: " + fotokopi.getJenisKertas());

        System.out.println();

        Pelanggan pelanggan = new Pelanggan(
                "Budi",
                "Sumenep",
                "08123456789"
        );

        Penjualan penjualan = new Penjualan(
                pelanggan,
                atk,
                2
        );

        System.out.println();

        penjualan.tampilkanTransaksi();
    }
}