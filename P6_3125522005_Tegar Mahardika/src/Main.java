public class Main {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println(" P6 - POLYMORPHISM & DYNAMIC BINDING");
        System.out.println("========================================");

        // ========================================
        // OBJECT SUBCLASS
        // ========================================

        ProdukATK atk = new ProdukATK(
                "Buku Tulis",
                5000,
                20,
                "Alat Tulis"
        );

        ProdukFotokopi fotokopi = new ProdukFotokopi(
                "Kertas A4",
                500,
                100,
                "A4"
        );

        ProdukATK atk2 = new ProdukATK(
                "Pulpen",
                3000,
                30,
                "Alat Tulis"
        );


        // ========================================
        // METHOD OVERRIDING
        // ========================================

        System.out.println("\n--- OVERRIDING ---");

        atk.tampilkanInfo();

        System.out.println();

        fotokopi.tampilkanInfo();


        // ========================================
        // UPCASTING / POLYMORPHIC REFERENCE
        // ========================================

        System.out.println("\n--- UPCASTING ---");

        Produk produk1 = new ProdukATK(
                "Pensil",
                2500,
                25,
                "Alat Tulis"
        );

        Produk produk2 = new ProdukFotokopi(
                "Kertas F4",
                700,
                80,
                "F4"
        );

        produk1.tampilkanInfo();

        System.out.println();

        produk2.tampilkanInfo();


        // ========================================
        // POLYMORPHIC COLLECTION
        // ========================================

        System.out.println("\n--- POLYMORPHIC COLLECTION ---");

        Produk[] daftarProduk = {
                atk,
                fotokopi,
                atk2
        };

        for (Produk produk : daftarProduk) {

            produk.tampilkanInfo();

            System.out.println();
        }


        // ========================================
        // METHOD OVERLOADING
        // ========================================

        System.out.println("--- METHOD OVERLOADING ---");

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

        System.out.println(
                "Total normal : Rp"
                + penjualan.hitungTotal(2)
        );

        System.out.println(
                "Total diskon : Rp"
                + penjualan.hitungTotal(2, 1000)
        );


        // ========================================
        // RELASI P4
        // ========================================

        System.out.println("\n--- RELASI P4 ---");

        penjualan.tampilkanTransaksi();


        // ========================================
        // DYNAMIC BINDING
        // ========================================

        System.out.println("\n--- DYNAMIC BINDING ---");

        Produk[] pengujian = {

                new ProdukATK(
                        "Map",
                        4000,
                        10,
                        "ATK"
                ),

                new ProdukFotokopi(
                        "Kertas Legal",
                        1000,
                        50,
                        "Legal"
                ),

                new ProdukATK(
                        "Penghapus",
                        2000,
                        15,
                        "ATK"
                )
        };


        for (Produk produk : pengujian) {

            produk.tampilkanInfo();

            System.out.println();
        }


        System.out.println(
                "Dynamic binding berhasil."
        );

        System.out.println(
                "Method tampilkanInfo() dipanggil"
        );

        System.out.println(
                "melalui reference Produk, tetapi"
        );

        System.out.println(
                "implementasi mengikuti object aktual."
        );
    }
}