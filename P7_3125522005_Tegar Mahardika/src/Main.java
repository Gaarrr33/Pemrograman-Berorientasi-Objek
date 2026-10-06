public class Main {

    public static void main(String[] args) {

        System.out.println("=================================================");
        System.out.println(" P7 - ABSTRACT CLASS, ABSTRACT METHOD & INTERFACE");
        System.out.println("=================================================");

        // ========================================================
        // 1. MEMBUAT OBJECT SUBCLASS (KONKRET)
        // ========================================================
        System.out.println("\n--- 1. OBJECT SUBCLASS ---");
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

        System.out.println("Object ProdukATK dan ProdukFotokopi berhasil diinstansiasi.");


        // ========================================================
        // 2. IMPLEMENTASI ABSTRACT METHOD & METHOD OVERRIDING
        // ========================================================
        System.out.println("\n--- 2. ABSTRACT METHOD & OVERRIDING ---");
        atk.tampilkanInfo();
        System.out.println();
        fotokopi.tampilkanInfo();


        // ========================================================
        // 3. UPCASTING / POLYMORPHIC REFERENCE (SUPERCLASS ABSTRACT)
        // ========================================================
        System.out.println("\n--- 3. UPCASTING KE ABSTRACT CLASS (Produk) ---");
        Produk produk1 = new ProdukATK(
                "Pensil 2B",
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


        // ========================================================
        // 4. PEMANGGILAN METHOD MELALUI REFERENCE INTERFACE
        // ========================================================
        System.out.println("\n--- 4. REFERENCE INTERFACE (DapatDiskon) ---");
        DapatDiskon diskon1 = new ProdukATK(
                "Spidol Whiteboard",
                8000,
                15,
                "Spidol"
        );

        DapatDiskon diskon2 = new ProdukFotokopi(
                "Kertas Buffalo",
                1200,
                50,
                "Buffalo"
        );

        System.out.println("Spidol Whiteboard (Diskon 10%) : Rp" + diskon1.hitungHargaDiskon(10));
        System.out.println("Kertas Buffalo    (Diskon 20%) : Rp" + diskon2.hitungHargaDiskon(20));


        // ========================================================
        // 5. POLYMORPHIC COLLECTION (ARRAY OF SUPERCLASS & INTERFACE)
        // ========================================================
        System.out.println("\n--- 5. POLYMORPHIC COLLECTION ---");
        Produk[] daftarProduk = {
                atk,
                fotokopi,
                atk2,
                produk1,
                produk2
        };

        for (Produk p : daftarProduk) {
            p.tampilkanInfo();
            System.out.println();
        }

        System.out.println("--- Array Bertipe Interface DapatDiskon ---");
        DapatDiskon[] daftarDiskon = {
                atk,
                fotokopi,
                atk2,
                diskon1,
                diskon2
        };

        for (DapatDiskon item : daftarDiskon) {
            if (item instanceof Produk) {
                Produk p = (Produk) item;
                System.out.println(p.getNamaProduk() + " (Harga Awal: Rp" + p.getHarga()
                        + " -> Diskon 15%: Rp" + item.hitungHargaDiskon(15) + ")");
            }
        }


        // ========================================================
        // 6. METHOD OVERLOADING & RELASI P4
        // ========================================================
        System.out.println("\n--- 6. METHOD OVERLOADING & RELASI P4 ---");
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

        System.out.println("Total normal (2 pcs)        : Rp" + penjualan.hitungTotal(2));
        System.out.println("Total diskon (2 pcs, -1000) : Rp" + penjualan.hitungTotal(2, 1000));
        System.out.println();
        penjualan.tampilkanTransaksi();


        // ========================================================
        // 7. DYNAMIC BINDING DEMONSTRATION
        // ========================================================
        System.out.println("\n--- 7. DYNAMIC BINDING ---");
        Produk[] pengujian = {
                new ProdukATK("Map Plastik", 4000, 10, "ATK"),
                new ProdukFotokopi("Kertas Legal", 1000, 50, "Legal"),
                new ProdukATK("Penghapus Box", 2000, 15, "ATK")
        };

        for (Produk p : pengujian) {
            p.tampilkanInfo();
            System.out.println();
        }

        System.out.println("Dynamic binding berhasil dibuktikan.");
        System.out.println("Method tampilkanInfo() didefinisikan sebagai abstract pada Produk,");
        System.out.println("dan dieksekusi sesuai perilaku subclass aktual saat runtime.");
    }
}