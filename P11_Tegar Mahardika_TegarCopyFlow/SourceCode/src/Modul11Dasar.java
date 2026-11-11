import java.util.ArrayList;
import java.util.List;

/**
 * MODUL 11 - Langkah Praktikum 1 s.d. 3
 * Studi Kasus: Pengelolaan Koleksi Buku Perpustakaan Menggunakan ArrayList
 * 
 * Meliputi seluruh skenario dasar dan pengujian tambahan sesuai instruksi Modul 11 (halaman 5):
 * 1. Pengujian dasar berurutan: Tambah B001 & B002, Cari B002, Tambah B001 duplikat, Hapus B001, Cari B001, Hapus B999.
 * 2. Pengujian tambahan: Judul kosong, Objek null, dan Menampilkan collection kosong.
 */
class BukuModul {
    private final String kode;
    private final String judul;

    public BukuModul(String kode, String judul) {
        if (kode == null || kode.trim().isEmpty() || judul == null || judul.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode dan judul wajib diisi.");
        }
        this.kode = kode.trim();
        this.judul = judul.trim();
    }

    public String getKode() {
        return kode;
    }

    public String getJudul() {
        return judul;
    }

    @Override
    public String toString() {
        return kode + " - " + judul;
    }
}

class PerpustakaanModul {
    private final List<BukuModul> daftarBuku = new ArrayList<>();

    public void tambahBuku(BukuModul buku) {
        if (buku == null) {
            throw new IllegalArgumentException("Objek buku tidak boleh null.");
        }
        if (cariBuku(buku.getKode()) != null) {
            throw new IllegalArgumentException("Kode buku sudah terdaftar.");
        }
        daftarBuku.add(buku);
    }

    public BukuModul cariBuku(String kode) {
        if (kode == null) {
            return null;
        }
        for (BukuModul buku : daftarBuku) {
            if (buku.getKode().equalsIgnoreCase(kode.trim())) {
                return buku;
            }
        }
        return null;
    }

    public boolean hapusBuku(String kode) {
        BukuModul buku = cariBuku(kode);
        if (buku == null) {
            return false;
        }
        return daftarBuku.remove(buku);
    }

    public void tampilkanBuku() {
        if (daftarBuku.isEmpty()) {
            System.out.println("Koleksi masih kosong.");
            return;
        }
        for (BukuModul buku : daftarBuku) {
            System.out.println(buku.getKode() + " - " + buku.getJudul());
        }
        System.out.println("Total buku: " + daftarBuku.size());
    }

    public int getJumlahBuku() {
        return daftarBuku.size();
    }
}

public class Modul11Dasar {
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("          MODUL PRAKTIKUM 11 - LANGKAH PRAKTIKUM 1 S.D. 3               ");
        System.out.println("         COLLECTION DAN PENGELOLAAN KUMPULAN OBJEK (ARRAYLIST)          ");
        System.out.println("========================================================================\n");

        PerpustakaanModul perpustakaan = new PerpustakaanModul();

        // ----------------------------------------------------------------------------------------
        // SKENARIO 1: Tambah B001 dan B002
        // ----------------------------------------------------------------------------------------
        System.out.println("[Skenario 1] Tambah B001 dan B002");
        perpustakaan.tambahBuku(new BukuModul("B001", "Dasar Java"));
        perpustakaan.tambahBuku(new BukuModul("B002", "Konsep OOP"));
        System.out.println("Hasil: Kedua buku berhasil tersimpan.");
        perpustakaan.tampilkanBuku();
        System.out.println("Jumlah data: " + perpustakaan.getJumlahBuku());
        System.out.println("------------------------------------------------------------------------");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 2: Cari B002
        // ----------------------------------------------------------------------------------------
        System.out.println("[Skenario 2] Cari B002");
        BukuModul hasilCari = perpustakaan.cariBuku("B002");
        if (hasilCari != null) {
            System.out.println("Hasil: Ditemukan: " + hasilCari.getJudul());
        } else {
            System.out.println("Hasil: Buku tidak ditemukan.");
        }
        System.out.println("Jumlah data: " + perpustakaan.getJumlahBuku());
        System.out.println("------------------------------------------------------------------------");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 3: Tambah B001 kembali (Uji Duplikasi Kode)
        // ----------------------------------------------------------------------------------------
        System.out.println("[Skenario 3] Tambah B001 kembali (Kode Duplikat)");
        try {
            perpustakaan.tambahBuku(new BukuModul("B001", "Buku Duplikat"));
            System.out.println("Hasil: Gagal (seharusnya melempar exception)!");
        } catch (IllegalArgumentException e) {
            System.out.println("Hasil: Ditolak: " + e.getMessage());
        }
        System.out.println("Jumlah data: " + perpustakaan.getJumlahBuku() + " (State tidak berubah)");
        System.out.println("------------------------------------------------------------------------");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 4: Hapus B001
        // ----------------------------------------------------------------------------------------
        System.out.println("[Skenario 4] Hapus B001");
        boolean hasilHapus = perpustakaan.hapusBuku("B001");
        System.out.println("Hasil penghapusan B001: " + hasilHapus);
        perpustakaan.tampilkanBuku();
        System.out.println("Jumlah data: " + perpustakaan.getJumlahBuku());
        System.out.println("------------------------------------------------------------------------");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 5: Cari B001 (Setelah dihapus)
        // ----------------------------------------------------------------------------------------
        System.out.println("[Skenario 5] Cari B001 (Setelah Dihapus)");
        BukuModul cariSetelahHapus = perpustakaan.cariBuku("B001");
        System.out.println("Hasil: " + (cariSetelahHapus == null ? "null (Buku tidak ditemukan)" : cariSetelahHapus.getJudul()));
        System.out.println("Jumlah data: " + perpustakaan.getJumlahBuku());
        System.out.println("------------------------------------------------------------------------");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 6: Hapus B999 (Kode tidak terdaftar)
        // ----------------------------------------------------------------------------------------
        System.out.println("[Skenario 6] Hapus B999 (Kode Tidak Terdaftar)");
        boolean hapusB999 = perpustakaan.hapusBuku("B999");
        System.out.println("Hasil penghapusan B999: " + hapusB999);
        System.out.println("Jumlah data: " + perpustakaan.getJumlahBuku());
        System.out.println("------------------------------------------------------------------------");

        // ----------------------------------------------------------------------------------------
        // SKENARIO TAMBAHAN SESUAI INSTRUKSI HALAMAN 5
        // ----------------------------------------------------------------------------------------
        System.out.println("[Skenario Tambahan 1] Pengujian Judul Kosong");
        try {
            BukuModul bukuSalah = new BukuModul("B003", "   ");
            perpustakaan.tambahBuku(bukuSalah);
            System.out.println("Hasil: Gagal (seharusnya melempar exception)!");
        } catch (IllegalArgumentException e) {
            System.out.println("Hasil: Ditolak: " + e.getMessage());
        }
        System.out.println("Jumlah data: " + perpustakaan.getJumlahBuku());
        System.out.println("------------------------------------------------------------------------");

        System.out.println("[Skenario Tambahan 2] Pengujian Objek Null");
        try {
            perpustakaan.tambahBuku(null);
            System.out.println("Hasil: Gagal (seharusnya melempar exception)!");
        } catch (IllegalArgumentException e) {
            System.out.println("Hasil: Ditolak: " + e.getMessage());
        }
        System.out.println("Jumlah data: " + perpustakaan.getJumlahBuku());
        System.out.println("------------------------------------------------------------------------");

        System.out.println("[Skenario Tambahan 3] Menampilkan Collection Kosong");
        // Hapus sisa buku B002 sehingga collection menjadi kosong
        perpustakaan.hapusBuku("B002");
        System.out.print("Memanggil tampilkanBuku() pada list kosong -> ");
        perpustakaan.tampilkanBuku();
        System.out.println("Jumlah data: " + perpustakaan.getJumlahBuku());
        System.out.println("========================================================================\n");
    }
}

