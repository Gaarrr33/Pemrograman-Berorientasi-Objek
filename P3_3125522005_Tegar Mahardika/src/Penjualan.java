public class Penjualan {
    private String kodeTransaksi;
    private Produk produk;
    private int jumlah;

    // Constructor
    public Penjualan(
            String kodeTransaksi,
            Produk produk,
            int jumlah) {

        setKodeTransaksi(kodeTransaksi);
        setProduk(produk);
        setJumlah(jumlah);
    }

    // Getter kode transaksi
    public String getKodeTransaksi() {
        return kodeTransaksi;
    }

    // Setter kode transaksi
    public void setKodeTransaksi(String kodeTransaksi) {
        if (kodeTransaksi != null && !kodeTransaksi.isBlank()) {
            this.kodeTransaksi = kodeTransaksi;
        } else {
            System.out.println("Kode transaksi tidak valid.");
        }
    }

    // Getter produk
    public Produk getProduk() {
        return produk;
    }

    // Setter produk
    public void setProduk(Produk produk) {
        if (produk != null) {
            this.produk = produk;
        } else {
            System.out.println("Produk tidak valid.");
        }
    }

    // Getter jumlah
    public int getJumlah() {
        return jumlah;
    }

    // Setter jumlah
    public void setJumlah(int jumlah) {
        if (jumlah > 0) {
            this.jumlah = jumlah;
        } else {
            System.out.println("Jumlah penjualan tidak valid. Jumlah harus lebih dari 0.");
        }
    }

    // Method menghitung total
    public double hitungTotal() {
        return produk.getHarga() * jumlah;
    }

    // Method menampilkan transaksi
    public void tampilkanTransaksi() {
        System.out.println("Kode Transaksi : " + kodeTransaksi);
        System.out.println("Produk         : " + produk.getNama());
        System.out.println("Harga          : Rp" + produk.getHarga());
        System.out.println("Jumlah         : " + jumlah);
        System.out.println("Total          : Rp" + hitungTotal());
    }

    // Method mengubah jumlah
    public void ubahJumlah(int jumlahBaru) {
        setJumlah(jumlahBaru);
    }
}