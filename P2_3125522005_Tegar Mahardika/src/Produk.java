public class Produk {
    String kode;
    String nama;
    double harga;

    public Produk(String kode, String nama, double harga) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
    }

    // Method tanpa parameter
    public void tampilkanData() {
        System.out.println("Kode  : " + kode);
        System.out.println("Nama  : " + nama);
        System.out.println("Harga : Rp" + harga);
    }

    // Method dengan parameter
    public void ubahHarga(double hargaBaru) {
        harga = hargaBaru;
    }

    // Method dengan return value
    public double getHarga() {
        return harga;
    }
}