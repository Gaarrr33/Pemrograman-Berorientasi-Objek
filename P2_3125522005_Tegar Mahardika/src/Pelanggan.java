public class Pelanggan {
    String idPelanggan;
    String nama;
    String noTelepon;

    public Pelanggan(String idPelanggan, String nama, String noTelepon) {
        this.idPelanggan = idPelanggan;
        this.nama = nama;
        this.noTelepon = noTelepon;
    }

    // Method tanpa parameter
    public void tampilkanNama() {
        System.out.println("ID Pelanggan : " + idPelanggan);
        System.out.println("Nama         : " + nama);
        System.out.println("No. Telepon  : " + noTelepon);
    }

    // Method dengan parameter
    public void ubahNama(String namaBaru) {
        nama = namaBaru;
    }

    // Method dengan return value
    public String getNama() {
        return nama;
    }
}