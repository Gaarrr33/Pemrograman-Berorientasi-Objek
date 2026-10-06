public abstract class Produk {
    private String namaProduk;
    private double harga;
    private int stok;

    public Produk(String namaProduk, double harga, int stok) {
        this.namaProduk = namaProduk;
        this.harga = harga;
        this.stok = stok;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public double getHarga() {
        return harga;
    }

    public int getStok() {
        return stok;
    }

    // Abstract Method: wajib diimplementasikan oleh setiap subclass konkret
    public abstract void tampilkanInfo();

    // Method Overloading
    public void tampilkanInfo(String label) {
        System.out.println(label);
        tampilkanInfo();
    }
}