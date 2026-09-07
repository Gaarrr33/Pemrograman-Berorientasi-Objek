public class Main {
    public static void main(String[] args) {

        // Object Buku
        Buku buku1 = new Buku(
            "B001",
            "Pemrograman Java",
            "Andi"
        );

        Buku buku2 = new Buku(
            "B002",
            "Algoritma dan Struktur Data",
            "Budi"
        );

        // Object Anggota
        Anggota anggota1 = new Anggota(
            "A001",
            "Garr",
            "Surabaya"
        );

        Anggota anggota2 = new Anggota(
            "A002",
            "Rizky",
            "Sumenep"
        );

        // Object Peminjaman
        Peminjaman peminjaman1 = new Peminjaman(
            "06-09-2026",
            buku1,
            anggota1
        );

        Peminjaman peminjaman2 = new Peminjaman(
            "06-09-2026",
            buku2,
            anggota2
        );

        System.out.println("===== DATA BUKU 1 =====");
        buku1.tampilkanData();

        System.out.println("\n===== DATA BUKU 2 =====");
        buku2.tampilkanData();

        System.out.println("\n===== DATA ANGGOTA 1 =====");
        anggota1.tampilkanData();

        System.out.println("\n===== DATA ANGGOTA 2 =====");
        anggota2.tampilkanData();

        System.out.println("\n===== DATA PEMINJAMAN 1 =====");
        peminjaman1.tampilkanData();
        peminjaman1.prosesPinjam();

        System.out.println("\n===== DATA PEMINJAMAN 2 =====");
        peminjaman2.tampilkanData();
        peminjaman2.prosesPinjam();

        // Pengujian method dengan parameter
        buku1.ubahJudul("Object Oriented Programming");

        anggota1.ubahAlamat("PENS Surabaya");

        // Pengujian return value
        System.out.println("\n===== HASIL PERUBAHAN =====");
        System.out.println("Judul buku 1 : " + buku1.getJudul());
        System.out.println("Nama anggota 1 : " + anggota1.getNama());
        System.out.println("Tanggal peminjaman 1 : " + peminjaman1.getTanggal());
    }
}