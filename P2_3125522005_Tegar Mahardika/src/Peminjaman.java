public class Peminjaman {
    String tanggal;
    Buku buku;
    Anggota anggota;

    public Peminjaman(String tanggal, Buku buku, Anggota anggota) {
        this.tanggal = tanggal;
        this.buku = buku;
        this.anggota = anggota;
    }

    public void prosesPinjam() {
        System.out.println("Peminjaman buku berhasil diproses.");
    }

    public void tampilkanData() {
        System.out.println("Tanggal : " + tanggal);
        System.out.println("Buku    : " + buku.getJudul());
        System.out.println("Anggota : " + anggota.getNama());
    }

    public String getTanggal() {
        return tanggal;
    }
}