public class Produk {
    private String kode;
    private String nama;
    private double harga;

    // Constructor
    public Produk(String kode, String nama, double harga) {
        setKode(kode);
        setNama(nama);
        setHarga(harga);
    }

    // Getter kode
    public String getKode() {
        return kode;
    }

    // Setter kode
    public void setKode(String kode) {
        if (kode != null && !kode.isBlank()) {
            this.kode = kode;
        } else {
            System.out.println("Kode produk tidak valid.");
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
            System.out.println("Nama produk tidak valid.");
        }
    }

    // Getter harga
    public double getHarga() {
        return harga;
    }

    // Setter harga
    public void setHarga(double harga) {
        if (harga > 0) {
            this.harga = harga;
        } else {
            System.out.println("Harga produk tidak valid. Harga harus lebih dari 0.");
        }
    }

    // Method menampilkan data
    public void tampilkanData() {
        System.out.println("Kode  : " + kode);
        System.out.println("Nama  : " + nama);
        System.out.println("Harga : Rp" + harga);
    }

    // Method mengubah harga
    public void ubahHarga(double hargaBaru) {
        setHarga(hargaBaru);
    }
}