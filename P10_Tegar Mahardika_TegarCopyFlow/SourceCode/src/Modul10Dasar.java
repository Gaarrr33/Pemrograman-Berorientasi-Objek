/**
 * MODUL 10 - Langkah Praktikum 1 s.d. 3
 * Studi Kasus: Pembelian Produk dengan Stok Awal 5 Unit
 * Dilengkapi pengujian tambahan "-1" dan "3" sesuai instruksi modul halaman 4.
 */
class StokTidakCukupExceptionModul extends Exception {
    public StokTidakCukupExceptionModul(String pesan) {
        super(pesan);
    }
}

class ProdukModul {
    private int stok;

    public ProdukModul(int stokAwal) {
        if (stokAwal < 0) {
            throw new IllegalArgumentException("Stok awal tidak boleh negatif.");
        }
        stok = stokAwal;
    }

    public void beli(int jumlah) throws StokTidakCukupExceptionModul {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pembelian harus lebih dari nol.");
        }
        if (jumlah > stok) {
            throw new StokTidakCukupExceptionModul("Stok tidak cukup. Tersedia: " + stok);
        }
        // State berubah setelah seluruh validasi berhasil.
        stok -= jumlah;
    }

    public int getStok() {
        return stok;
    }
}

public class Modul10Dasar {
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("          MODUL PRAKTIKUM 10 - LANGKAH PRAKTIKUM 1 S.D. 3               ");
        System.out.println("            STUDI KASUS VALIDASI PEMBELIAN PRODUK (STOK: 5)             ");
        System.out.println("========================================================================\n");

        ProdukModul produk = new ProdukModul(5);
        // Daftar input awal: "2", "0", "9", "abc"
        // Ditambah pengujian "-1" dan "3" sesuai instruksi Langkah 3 Modul 10
        String[] daftarInput = {"2", "0", "9", "abc", "-1", "3"};

        for (String input : daftarInput) {
            System.out.println("Input pembelian: \"" + input + "\"");
            try {
                int jumlah = Integer.parseInt(input);
                produk.beli(jumlah);
                System.out.println("Hasil          : Pembelian berhasil.");
            } catch (NumberFormatException e) {
                System.out.println("Hasil          : Ditolak: input harus bilangan bulat.");
            } catch (IllegalArgumentException e) {
                System.out.println("Hasil          : Ditolak: " + e.getMessage());
            } catch (StokTidakCukupExceptionModul e) {
                System.out.println("Hasil          : Ditolak: " + e.getMessage());
            } finally {
                System.out.println("Stok akhir     : " + produk.getStok());
                System.out.println("------------------------------------------------------------------------");
            }
        }

        System.out.println("\nPenjelasan Pengujian Nilai 3:");
        System.out.println("Pembelian tepat sejumlah sisa stok (3 unit dari 3 unit) diperbolehkan karena");
        System.out.println("aturan validasi adalah (jumlah > stok). Menghabiskan seluruh stok yang tersedia");
        System.out.println("merupakan skenario bisnis normal (stok akhir = 0) dan invarian state objek tetap valid.");
    }
}

