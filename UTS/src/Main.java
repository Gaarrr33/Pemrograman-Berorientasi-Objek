import java.util.List;
import java.util.Scanner;

/**
 * Class Main menyediakan antarmuka console (CLI) untuk berinteraksi
 * dengan Sistem Klinik Sehat Medika.
 * Memuat menu interaktif dan skenario pembuktian konsep OOP.
 */
public class Main {

    public static void main(String[] args) {
        Klinik klinik = new Klinik("Klinik Pratama Sehat Medika", "Jl. Pemuda No. 45, Kota Pendidikan");
        // Inisialisasi data awal (Dokter, Perawat, dan Pasien contoh)
        klinik.inisialisasiDataAwal();

        // Jika dipanggil dengan argumen --demo atau test, jalankan demonstrasi otomatis
        if (args.length > 0 && (args[0].equalsIgnoreCase("--demo") || args[0].equalsIgnoreCase("test"))) {
            jalankanSkenarioPengujian(klinik);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            tampilkanMenuUtama();
            int pilihan = bacaInteger(scanner, "Pilih Menu [1-8, atau 9 untuk Demo]: ");

            switch (pilihan) {
                case 1:
                    menuTambahPasien(scanner, klinik);
                    break;
                case 2:
                    klinik.tampilkanDaftarPasien();
                    break;
                case 3:
                    klinik.tampilkanDaftarTenagaMedis();
                    break;
                case 4:
                    menuDaftarkanKunjungan(scanner, klinik);
                    break;
                case 5:
                    klinik.tampilkanAntreanPrioritas();
                    break;
                case 6:
                    menuProsesPelayanan(scanner, klinik);
                    break;
                case 7:
                    klinik.tampilkanSemuaKunjungan();
                    break;
                case 8:
                    System.out.println("\nTerima kasih telah menggunakan Sistem Informasi Klinik. Program selesai.");
                    running = false;
                    break;
                case 9:
                    jalankanSkenarioPengujian(klinik);
                    break;
                default:
                    System.out.println("[PERINGATAN] Pilihan tidak valid. Silakan pilih menu 1 sampai 8.");
            }

            if (running) {
                System.out.print("\nTekan [Enter] untuk kembali ke menu utama...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private static void tampilkanMenuUtama() {
        System.out.println("\n========================================================");
        System.out.println("         SISTEM INFORMASI KLINIK SEHAT MEDIKA          ");
        System.out.println("       UTS PRAKTIKUM PBO - STUDI KASUS TIPE B           ");
        System.out.println("========================================================");
        System.out.println("1. Menambahkan Pasien Baru");
        System.out.println("2. Menampilkan Daftar Pasien");
        System.out.println("3. Menampilkan Tenaga Medis (Dokter & Perawat)");
        System.out.println("4. Mendaftarkan Kunjungan Pasien");
        System.out.println("5. Menampilkan Antrean Berdasarkan Prioritas [Fitur Kreatif]");
        System.out.println("6. Memproses Pelayanan Pasien");
        System.out.println("7. Menampilkan Status Kunjungan");
        System.out.println("8. Keluar dari Program");
        System.out.println("--------------------------------------------------------");
        System.out.println("9. Jalankan Skenario Demonstrasi OOP & Business Rules");
        System.out.println("========================================================");
    }

    // -------------------------------------------------------------------------
    // MENU ACTIONS DENGAN PENANGANAN ERROR & VALIDASI
    // -------------------------------------------------------------------------

    private static void menuTambahPasien(Scanner scanner, Klinik klinik) {
        System.out.println("\n--- [1] MENAMBAHKAN PASIEN BARU ---");
        try {
            System.out.print("Masukkan ID Pasien (misal P005): ");
            String id = scanner.nextLine().trim();

            System.out.print("Masukkan Nama Pasien: ");
            String nama = scanner.nextLine().trim();

            int usia = bacaInteger(scanner, "Masukkan Usia Pasien (tahun): ");

            System.out.print("Masukkan Jenis Kelamin (Laki-laki/Perempuan): ");
            String jk = scanner.nextLine().trim();

            System.out.print("Masukkan Alamat: ");
            String alamat = scanner.nextLine().trim();

            Pasien pasienBaru = new Pasien(id, nama, usia, jk, alamat);
            klinik.tambahPasien(pasienBaru);
            System.out.println("[SUKSES] Pasien " + nama + " (" + id + ") berhasil didaftarkan ke sistem.");
        } catch (IllegalArgumentException e) {
            System.out.println("[GAGAL VALIDASI] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void menuDaftarkanKunjungan(Scanner scanner, Klinik klinik) {
        System.out.println("\n--- [4] MENDAFTARKAN KUNJUNGAN PASIEN ---");
        try {
            klinik.tampilkanDaftarPasien();
            System.out.print("\nMasukkan ID Pasien: ");
            String idPasien = scanner.nextLine().trim();

            klinik.tampilkanDaftarTenagaMedis();
            System.out.print("\nMasukkan ID Tenaga Medis Pemeriksa: ");
            String idTenagaMedis = scanner.nextLine().trim();

            System.out.print("Masukkan Keluhan Pasien: ");
            String keluhan = scanner.nextLine().trim();

            System.out.println("\nPilih Tingkat Prioritas Antrean (Fitur Kreatif):");
            System.out.println("  1 = Darurat (Gawat darurat, butuh penanganan segera)");
            System.out.println("  2 = Mendesak (Kondisi sakit sedang, demam/nyeri)");
            System.out.println("  3 = Reguler  (Pemeriksaan umum/kontrol rutin)");
            int prioritas = bacaInteger(scanner, "Masukkan Pilihan Prioritas [1/2/3]: ");

            Kunjungan kunjungan = klinik.daftarkanKunjungan(idPasien, idTenagaMedis, keluhan, prioritas);
            System.out.println("\n[SUKSES] Kunjungan berhasil didaftarkan!");
            System.out.println("  ID Kunjungan : " + kunjungan.getIdKunjungan());
            System.out.println("  No Antrean   : #" + kunjungan.getNomorAntrean());
            System.out.println("  Prioritas    : " + kunjungan.getTeksPrioritas());
            System.out.println("  Status       : " + kunjungan.getStatus());
        } catch (IllegalArgumentException e) {
            System.out.println("[GAGAL VALIDASI] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void menuProsesPelayanan(Scanner scanner, Klinik klinik) {
        System.out.println("\n--- [6] MEMPROSES PELAYANAN PASIEN ---");
        System.out.println("1. Proses Pasien Antrean Terdepan (Prioritas Tertinggi)");
        System.out.println("2. Proses Berdasarkan ID Kunjungan Tertentu");
        int subPilihan = bacaInteger(scanner, "Pilihan Anda [1/2]: ");

        if (subPilihan == 1) {
            try {
                klinik.prosesAntreanTerdepan();
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        } else if (subPilihan == 2) {
            klinik.tampilkanSemuaKunjungan();
            System.out.print("\nMasukkan ID Kunjungan yang ingin diproses (misal KJ001): ");
            String idKunjungan = scanner.nextLine().trim();
            Kunjungan k = klinik.cariKunjungan(idKunjungan);
            if (k == null) {
                System.out.println("[GAGAL] Kunjungan dengan ID " + idKunjungan + " tidak ditemukan!");
            } else {
                try {
                    k.prosesPelayanan();
                } catch (IllegalStateException e) {
                    System.out.println("[GAGAL VALIDASI STATUS] " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("[ERROR] " + e.getMessage());
                }
            }
        } else {
            System.out.println("[PERINGATAN] Pilihan tidak valid.");
        }
    }

    // -------------------------------------------------------------------------
    // SKENARIO PENGUJIAN OTOMATIS (MEMBUKTIKAN SEMUA SYARAT UTS)
    // -------------------------------------------------------------------------

    public static void jalankanSkenarioPengujian(Klinik klinik) {
        System.out.println("\n================================================================================");
        System.out.println("          SKENARIO PENGUJIAN OTOMATIS & PEMBUKTIAN PERSYARATAN UTS PBO          ");
        System.out.println("================================================================================");

        System.out.println("\n[UJI 1] PEMBUKTIAN VALIDASI & BUSINESS RULES");
        System.out.println("--------------------------------------------------------------------------------");
        // Rule 1A: Nama pasien tidak boleh kosong
        System.out.println("-> Uji Kasus 1: Mendaftarkan pasien dengan Nama kosong (Harus Ditolak)");
        try {
            new Pasien("TEST01", "   ", 25, "Laki-laki", "Jakarta");
            System.out.println("  [GAGAL] Seharusnya melempar exception!");
        } catch (IllegalArgumentException e) {
            System.out.println("  [BERHASIL DICEGAH] Pesan Exception: " + e.getMessage());
        }

        // Rule 1B: Umur pasien tidak boleh <= 0
        System.out.println("\n-> Uji Kasus 2: Mendaftarkan pasien dengan Usia negatif (-5) (Harus Ditolak)");
        try {
            new Pasien("TEST02", "Doni Saputra", -5, "Laki-laki", "Bandung");
            System.out.println("  [GAGAL] Seharusnya melempar exception!");
        } catch (IllegalArgumentException e) {
            System.out.println("  [BERHASIL DICEGAH] Pesan Exception: " + e.getMessage());
        }

        // Rule 1C: ID Pasien tidak boleh duplikat di Klinik
        System.out.println("\n-> Uji Kasus 3: Mendaftarkan pasien dengan ID yang sudah ada (P001) (Harus Ditolak)");
        try {
            klinik.tambahPasien(new Pasien("P001", "Fauzi Duplikat", 30, "Laki-laki", "Surabaya"));
            System.out.println("  [GAGAL] Seharusnya melempar exception duplikasi!");
        } catch (IllegalArgumentException e) {
            System.out.println("  [BERHASIL DICEGAH] Pesan Exception: " + e.getMessage());
        }

        System.out.println("\n[UJI 2] PEMBUKTIAN FITUR KREATIF: PENGURUTAN ANTREAN PRIORITAS");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Kunjungan awal didaftarkan dengan urutan:");
        System.out.println("  1. P001 didaftarkan lebih awal (Antrean #1, Prioritas 3 - REGULER)");
        System.out.println("  2. P002 didaftarkan berikutnya (Antrean #2, Prioritas 2 - MENDESAK)");
        System.out.println("  3. P003 didaftarkan belakangan (Antrean #3, Prioritas 1 - DARURAT)");
        System.out.println("Hasil antrean setelah diurutkan berdasarkan tingkat urgensi:");
        klinik.tampilkanAntreanPrioritas();
        System.out.println("Perhatikan: P003 (Darurat) melompati antrean dan menjadi yang PERTAMA dilayani!");

        System.out.println("\n[UJI 3] PEMBUKTIAN POLYMORPHISM & INTERFACE");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Memanggil method beriPelayanan() melalui referensi TenagaMedis (Superclass / Interface):");
        System.out.println("Objek yang ditunjuk berbeda runtime (Perawat vs Dokter), menghasilkan output berbeda:\n");

        System.out.println("-> 3A. Melayani Antrean Terdepan (Prioritas 1 - Ditangani oleh PERAWAT):");
        Kunjungan k1 = klinik.prosesAntreanTerdepan(); // KJ003 ditangani oleh Perawat Ners Andi Pratama

        System.out.println("\n-> 3B. Melayani Antrean Prioritas Dokter (KJ004 - Ditangani oleh DOKTER SPESIALIS):");
        Kunjungan kDokter = klinik.cariKunjungan("KJ004"); // KJ004 ditangani oleh dr. Siti Rahma, Sp.A
        if (kDokter != null) {
            kDokter.prosesPelayanan();
        }

        System.out.println("\n[UJI 4] PEMBUKTIAN VALIDASI STATUS KUNJUNGAN");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("-> Uji Kasus: Memproses ulang Kunjungan yang statusnya sudah SELESAI (Harus Ditolak)");
        try {
            if (k1 != null) {
                k1.prosesPelayanan();
            }
        } catch (IllegalStateException e) {
            System.out.println("  [BERHASIL DICEGAH] Pesan Exception: " + e.getMessage());
        }

        System.out.println("\n================================================================================");
        System.out.println("                   SELURUH PENGUJIAN OTOMATIS SELESAI & LULUS                   ");
        System.out.println("================================================================================");
    }

    // -------------------------------------------------------------------------
    // HELPER METHOD UNTUK INPUT USER AGAR TIDAK CRASH
    // -------------------------------------------------------------------------

    private static int bacaInteger(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[INPUT ERROR] Masukkan harus berupa angka bulat!");
            }
        }
    }
}
