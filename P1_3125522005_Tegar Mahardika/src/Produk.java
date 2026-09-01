public class Produk {
    String kode;
    String nama;
    double harga;

    void tampilkanData() {
        System.out.println("Kode  : " + kode);
        System.out.println("Nama  : " + nama);
        System.out.println("Harga : Rp" + harga);
    }
}