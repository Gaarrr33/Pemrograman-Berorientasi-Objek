public class Penjualan {

    private String kodeTransaksi;
    private Pelanggan pelanggan;
    private Produk produk;
    private int jumlah;
    private DetailPenjualan detail;

    public Penjualan(
        String kodeTransaksi,
        Pelanggan pelanggan,
        Produk produk,
        int jumlah
    ) {
        setKodeTransaksi(kodeTransaksi);
        setPelanggan(pelanggan);
        setProduk(produk);
        setJumlah(jumlah);

        // Composition
        this.detail = new DetailPenjualan(produk, jumlah);
    }

    public String getKodeTransaksi() {
        return kodeTransaksi;
    }

    public void setKodeTransaksi(String kodeTransaksi) {
        if (kodeTransaksi == null ||
            kodeTransaksi.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "Kode transaksi tidak boleh kosong."
            );
        }

        this.kodeTransaksi = kodeTransaksi;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public void setPelanggan(Pelanggan pelanggan) {
        if (pelanggan == null) {
            throw new IllegalArgumentException(
                "Pelanggan tidak boleh null."
            );
        }

        this.pelanggan = pelanggan;
    }

    public Produk getProduk() {
        return produk;
    }

    public void setProduk(Produk produk) {
        if (produk == null) {
            throw new IllegalArgumentException(
                "Produk tidak boleh null."
            );
        }

        this.produk = produk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException(
                "Jumlah harus lebih dari 0."
            );
        }

        this.jumlah = jumlah;
    }

    public DetailPenjualan getDetail() {
        return detail;
    }

    public void ubahJumlah(int jumlahBaru) {
        setJumlah(jumlahBaru);
        this.detail = new DetailPenjualan(produk, jumlahBaru);
    }

    public double hitungTotal() {
        return produk.getHarga() * jumlah;
    }

    public void tampilkanTransaksi() {
        System.out.println("===== DATA TRANSAKSI =====");
        System.out.println("Kode Transaksi : " + kodeTransaksi);
        System.out.println("Pelanggan      : " + pelanggan.getNama());
        System.out.println("Produk         : " + produk.getNama());
        System.out.println("Harga          : Rp" + produk.getHarga());
        System.out.println("Jumlah         : " + jumlah);
        System.out.println("Total          : Rp" + hitungTotal());
    }
}