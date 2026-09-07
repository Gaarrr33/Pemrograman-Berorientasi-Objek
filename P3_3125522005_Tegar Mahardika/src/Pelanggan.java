public class Pelanggan {
    private String idPelanggan;
    private String nama;
    private String noTelepon;

    // Constructor
    public Pelanggan(String idPelanggan, String nama, String noTelepon) {
        setIdPelanggan(idPelanggan);
        setNama(nama);
        setNoTelepon(noTelepon);
    }

    // Getter ID pelanggan
    public String getIdPelanggan() {
        return idPelanggan;
    }

    // Setter ID pelanggan
    public void setIdPelanggan(String idPelanggan) {
        if (idPelanggan != null && !idPelanggan.isBlank()) {
            this.idPelanggan = idPelanggan;
        } else {
            System.out.println("ID pelanggan tidak valid.");
        }
    }

    // Getter nama
    public String getNama() {
        return nama;
    }

    // Setter nama
    public void setNama(String nama) {
        if (nama != null && !nama.isBlank()) {
            this.nama = nama;
        } else {
            System.out.println("Nama pelanggan tidak valid.");
        }
    }

    // Getter nomor telepon
    public String getNoTelepon() {
        return noTelepon;
    }

    // Setter nomor telepon
    public void setNoTelepon(String noTelepon) {
        if (noTelepon != null
                && !noTelepon.isBlank()
                && noTelepon.matches("\\d+")) {

            this.noTelepon = noTelepon;

        } else {
            System.out.println("Nomor telepon tidak valid.");
        }
    }

    // Method menampilkan nama
    public void tampilkanNama() {
        System.out.println("Nama pelanggan: " + nama);
    }

    // Method mengubah nama
    public void ubahNama(String namaBaru) {
        setNama(namaBaru);
    }
}