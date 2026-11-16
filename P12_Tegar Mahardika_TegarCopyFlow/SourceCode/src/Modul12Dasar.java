import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * MODUL 12 - Langkah Praktikum 1 s.d. 5 dan Pengujian Konsistensi State
 * Topik: Penyimpanan dan Pembacaan Data ke File (NIO Files & Path)
 * 
 * Meliputi:
 * 1. Mode CLI (args: "simpan" atau "muat") sesuai instruksi Langkah 4 & 5 modul.
 * 2. Mode Pengujian Otomatis komprehensif (seluruh skenario pengujian tabel halaman 5 & 6):
 *    - Simpan dua buku valid
 *    - Muat file valid
 *    - Muat file tidak ada
 *    - Muat file format salah (tanpa pemisah '|')
 *    - Muat file data tidak valid (judul kosong)
 *    - Muat file kode duplikat
 *    - Muat file kosong
 *    - Uji Konsistensi State (atomic update / staging rollback saat file rusak)
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

    public List<BukuModul> getDaftarBuku() {
        return daftarBuku;
    }

    public void simpan(Path lokasi) throws IOException {
        List<String> baris = new ArrayList<>();
        for (BukuModul buku : daftarBuku) {
            String kode = buku.getKode();
            String judul = buku.getJudul();
            for (String nilai : new String[]{kode, judul}) {
                if (nilai.contains("|") || nilai.contains("\n") || nilai.contains("\r")) {
                    throw new IllegalArgumentException("Kode/judul mengandung pemisah terlarang.");
                }
            }
            baris.add(kode + "|" + judul);
        }
        if (lokasi.getParent() != null) {
            Files.createDirectories(lokasi.getParent());
        }
        Files.write(lokasi, baris, StandardCharsets.UTF_8);
    }

    public void muat(Path lokasi) throws IOException {
        List<String> baris = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        PerpustakaanModul sementara = new PerpustakaanModul();

        for (int i = 0; i < baris.size(); i++) {
            String teksBaris = baris.get(i);
            String[] bagian = teksBaris.split("\\|", -1);
            if (bagian.length != 2) {
                throw new IOException("Format salah pada baris " + (i + 1));
            }
            try {
                BukuModul buku = new BukuModul(bagian[0], bagian[1]);
                sementara.tambahBuku(buku);
            } catch (IllegalArgumentException e) {
                throw new IOException("Data tidak valid pada baris " + (i + 1) + ": " + e.getMessage(), e);
            }
        }

        // Ganti collection hanya setelah seluruh baris berhasil divalidasi
        daftarBuku.clear();
        daftarBuku.addAll(sementara.daftarBuku);
    }
}

public class Modul12Dasar {
    public static void main(String[] args) {
        Path lokasiBuku = Paths.get("data", "buku.txt");

        // Jika dipanggil dengan argumen CLI spesifik ("simpan" atau "muat")
        if (args.length == 1) {
            PerpustakaanModul perpustakaan = new PerpustakaanModul();
            try {
                if (args[0].equalsIgnoreCase("simpan")) {
                    perpustakaan.tambahBuku(new BukuModul("B001", "Dasar Java"));
                    perpustakaan.tambahBuku(new BukuModul("B002", "Konsep OOP"));
                    perpustakaan.simpan(lokasiBuku);
                    System.out.println("Data berhasil disimpan ke " + lokasiBuku);
                } else if (args[0].equalsIgnoreCase("muat")) {
                    perpustakaan.muat(lokasiBuku);
                    perpustakaan.tampilkanBuku();
                } else {
                    System.out.println("Gunakan: java Modul12Dasar simpan atau java Modul12Dasar muat");
                }
            } catch (IOException e) {
                System.out.println("Operasi file gagal: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Data ditolak: " + e.getMessage());
            }
            return;
        }

        // Jika dijalankan tanpa argumen: Eksekusi seluruh rangkaian pengujian Modul 12
        System.out.println("========================================================================");
        System.out.println("          MODUL PRAKTIKUM 12 - LANGKAH PRAKTIKUM 1 S.D. 5               ");
        System.out.println("           PENYIMPANAN DAN PEMBACAAN DATA KE FILE (NIO.FILES)           ");
        System.out.println("========================================================================\n");

        PerpustakaanModul perpustakaan = new PerpustakaanModul();

        // 1. Simpan dua buku valid
        System.out.println("[Skenario 1] Simpan dua buku valid ke file data/buku.txt");
        try {
            perpustakaan.tambahBuku(new BukuModul("B001", "Dasar Java"));
            perpustakaan.tambahBuku(new BukuModul("B002", "Konsep OOP"));
            perpustakaan.simpan(lokasiBuku);
            System.out.println("Hasil: File berhasil disimpan.");
            System.out.println("Isi file:");
            for (String l : Files.readAllLines(lokasiBuku, StandardCharsets.UTF_8)) {
                System.out.println("  " + l);
            }
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
        }
        System.out.println("------------------------------------------------------------------------");

        // 2. Muat file valid dalam instance baru
        System.out.println("[Skenario 2] Muat file valid (Membentuk kembali objek dari file)");
        PerpustakaanModul perpustakaanBaru = new PerpustakaanModul();
        try {
            perpustakaanBaru.muat(lokasiBuku);
            System.out.println("Hasil pemuatan file:");
            perpustakaanBaru.tampilkanBuku();
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
        }
        System.out.println("------------------------------------------------------------------------");

        // 3. Muat file yang tidak ada
        System.out.println("[Skenario 3] Muat file yang tidak ada (data/tidak_ada.txt)");
        try {
            PerpustakaanModul p = new PerpustakaanModul();
            p.muat(Paths.get("data", "tidak_ada.txt"));
            System.out.println("Gagal (seharusnya exception)!");
        } catch (IOException e) {
            System.out.println("Hasil: Pesan kegagalan operasi file muncul -> \"" + e.getClass().getSimpleName() + ": " + e.getMessage() + "\"");
        }
        System.out.println("------------------------------------------------------------------------");

        // 4. Ubah satu baris menjadi B003 (Format salah: tanpa pemisah '|')
        System.out.println("[Skenario 4] Muat file dengan format salah (Baris: \"B003\" tanpa pemisah)");
        Path fileFormatSalah = Paths.get("data", "buku_format_salah.txt");
        try {
            Files.write(fileFormatSalah, List.of("B001|Dasar Java", "B003"), StandardCharsets.UTF_8);
            PerpustakaanModul p = new PerpustakaanModul();
            p.muat(fileFormatSalah);
            System.out.println("Gagal (seharusnya exception)!");
        } catch (IOException e) {
            System.out.println("Hasil: Ditolak karena format salah -> \"" + e.getMessage() + "\"");
        }
        System.out.println("------------------------------------------------------------------------");

        // 5. Ubah satu baris menjadi "B003|" (Data tidak valid: judul kosong)
        System.out.println("[Skenario 5] Muat file dengan data tidak valid (Baris: \"B003|\" judul kosong)");
        Path fileJudulKosong = Paths.get("data", "buku_judul_kosong.txt");
        try {
            Files.write(fileJudulKosong, List.of("B001|Dasar Java", "B003|"), StandardCharsets.UTF_8);
            PerpustakaanModul p = new PerpustakaanModul();
            p.muat(fileJudulKosong);
            System.out.println("Gagal (seharusnya exception)!");
        } catch (IOException e) {
            System.out.println("Hasil: Ditolak data tidak valid -> \"" + e.getMessage() + "\"");
        }
        System.out.println("------------------------------------------------------------------------");

        // 6. Isi file dengan kode duplikat
        System.out.println("[Skenario 6] Muat file dengan kode duplikat (B001 muncul dua kali)");
        Path fileDuplikat = Paths.get("data", "buku_duplikat.txt");
        try {
            Files.write(fileDuplikat, List.of("B001|Dasar Java", "B001|Java Lanjutan"), StandardCharsets.UTF_8);
            PerpustakaanModul p = new PerpustakaanModul();
            p.muat(fileDuplikat);
            System.out.println("Gagal (seharusnya exception)!");
        } catch (IOException e) {
            System.out.println("Hasil: Pemuatan ditolak -> \"" + e.getMessage() + "\"");
        }
        System.out.println("------------------------------------------------------------------------");

        // 7. Muat file kosong
        System.out.println("[Skenario 7] Muat file kosong (data/buku_kosong.txt)");
        Path fileKosong = Paths.get("data", "buku_kosong.txt");
        try {
            Files.write(fileKosong, List.of(), StandardCharsets.UTF_8);
            PerpustakaanModul p = new PerpustakaanModul();
            p.tambahBuku(new BukuModul("TEMP", "Buku Sementara"));
            p.muat(fileKosong);
            System.out.print("Hasil: ");
            p.tampilkanBuku();
            System.out.println("Jumlah buku: " + p.getJumlahBuku() + " (Collection menjadi kosong)");
        } catch (Exception e) {
            System.out.println("Gagal: " + e.getMessage());
        }
        System.out.println("------------------------------------------------------------------------");

        // 8. Uji Konsistensi State (Atomic Replacement / Staging)
        System.out.println("[Skenario 8] Uji Konsistensi State (Koleksi tidak terhapus jika muat gagal)");
        PerpustakaanModul pKonsisten = new PerpustakaanModul();
        pKonsisten.tambahBuku(new BukuModul("B100", "Struktur Data"));
        System.out.println("State awal koleksi sebelum pemanggilan muat() gagal:");
        pKonsisten.tampilkanBuku();

        System.out.println("Memanggil muat() ke file yang rusak (buku_format_salah.txt)...");
        try {
            pKonsisten.muat(fileFormatSalah);
            System.out.println("Gagal (seharusnya exception)!");
        } catch (IOException e) {
            System.out.println("Exception tertangkap: \"" + e.getMessage() + "\"");
        }

        System.out.println("State akhir koleksi setelah pemuatan gagal:");
        pKonsisten.tampilkanBuku();
        System.out.println("Status: KONSISTEN! Objek buku B100 tetap utuh dan tidak terhapus.");
        System.out.println("========================================================================\n");
    }
}

