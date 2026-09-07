public class Buku {
    String kode;
    String judul;
    String penulis;

    public Buku(String kode, String judul, String penulis) {
        this.kode = kode;
        this.judul = judul;
        this.penulis = penulis;
    }

    public void tampilkanData() {
        System.out.println("Kode    : " + kode);
        System.out.println("Judul   : " + judul);
        System.out.println("Penulis : " + penulis);
    }

    public void ubahJudul(String judulBaru) {
        judul = judulBaru;
    }

    public String getJudul() {
        return judul;
    }
}