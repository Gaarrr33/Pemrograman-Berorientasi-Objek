package model;

/**
 * Class DetailTransaksi merepresentasikan satu baris item dalam sebuah transaksi kasir.
 * Memiliki relasi Association dengan class Produk.
 * Bertanggung jawab menghitung subtotal penjualan, menyimpan nilai COGS (HPP),
 * dan menghitung laba kotor (gross profit) per baris item.
 */
public class DetailTransaksi {
    private Produk produk;
    private String namaProduk;
    private String tipeProduk;
    private int jumlah;
    private double hargaSatuan;
    private double subtotal;
    private double cogs;
    private double grossProfit;

    public DetailTransaksi(Produk produk, int jumlah, double hargaSatuan, double cogs) {
        if (produk == null) {
            throw new IllegalArgumentException("Objek produk tidak boleh null pada baris detail transaksi.");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah item belanja harus lebih dari 0.");
        }
        if (hargaSatuan < 0) {
            throw new IllegalArgumentException("Harga satuan produk tidak boleh bernilai negatif.");
        }
        if (cogs < 0) {
            throw new IllegalArgumentException("Nilai HPP (COGS) barang tidak boleh bernilai negatif.");
        }

        this.produk = produk;
        this.namaProduk = produk.getNama();
        this.tipeProduk = produk.getTipe();
        this.jumlah = jumlah;
        this.hargaSatuan = hargaSatuan;
        this.cogs = cogs;
        this.subtotal = hitungSubtotal();
        this.grossProfit = hitungGrossProfit();
    }

    public double hitungSubtotal() {
        return this.jumlah * this.hargaSatuan;
    }

    public double hitungGrossProfit() {
        return this.subtotal - this.cogs;
    }

    // Encapsulation Getters
    public Produk getProduk() {
        return produk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public String getTipeProduk() {
        return tipeProduk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public double getHargaSatuan() {
        return hargaSatuan;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getCogs() {
        return cogs;
    }

    public double getGrossProfit() {
        return grossProfit;
    }

    @Override
    public String toString() {
        return String.format("%-25s | %3d %-6s | Rp %,9.0f | Rp %,11.0f | HPP: Rp %,10.0f | Margin: Rp %,9.0f",
                namaProduk, jumlah, produk.getSatuan(), hargaSatuan, subtotal, cogs, grossProfit);
    }
}

