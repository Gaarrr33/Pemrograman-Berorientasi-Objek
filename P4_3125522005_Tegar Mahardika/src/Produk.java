public class Produk {

    private String kode;
    private String nama;
    private double harga;

    public Produk(String kode, String nama, double harga) {
        setKode(kode);
        setNama(nama);
        setHarga(harga);
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode produk tidak boleh kosong.");
        }
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama produk tidak boleh kosong.");
        }
        this.nama = nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga harus lebih dari 0.");
        }
        this.harga = harga;
    }

    public void ubahHarga(double hargaBaru) {
        setHarga(hargaBaru);
    }

    public void tampilkanData() {
        System.out.println("Kode Produk : " + kode);
        System.out.println("Nama Produk : " + nama);
        System.out.println("Harga       : Rp" + harga);
    }
}