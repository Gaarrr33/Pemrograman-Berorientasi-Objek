package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class TransaksiPenjualan merepresentasikan transaksi kasir (POS)
 * pada sistem Tegar CopyFlow (sesuai tabel sales dan logika sales.js).
 * Memiliki relasi Composition dengan DetailTransaksi (satu transaksi memiliki 1..* item detail).
 * Mengorkestrasikan kalkulasi total belanja, akumulasi HPP (COGS),
 * perhitungan laba kotor transaksi, serta pembatalan transaksi dengan rollback stok.
 */
public class TransaksiPenjualan {
    private String invoiceNumber;
    private String tanggalTransaksi;
    private String paymentMethod; // "Cash", "QRIS", "Transfer", "Other"
    private String status;        // "COMPLETED", "CANCELLED"
    private String catatan;
    private List<DetailTransaksi> daftarItem;
    private double total;
    private double totalCogs;
    private double grossProfit;

    public TransaksiPenjualan(String invoiceNumber, String tanggalTransaksi, String paymentMethod, String catatan) {
        if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor invoice tidak boleh kosong.");
        }
        if (!isValidPaymentMethod(paymentMethod)) {
            throw new IllegalArgumentException("Metode pembayaran harus salah satu dari: Cash, QRIS, Transfer, Other.");
        }

        this.invoiceNumber = invoiceNumber.trim();
        this.tanggalTransaksi = (tanggalTransaksi != null && !tanggalTransaksi.trim().isEmpty())
                ? tanggalTransaksi.trim()
                : "2026-10-26";
        this.paymentMethod = paymentMethod.trim();
        this.status = "COMPLETED";
        this.catatan = (catatan != null) ? catatan.trim() : "-";
        this.daftarItem = new ArrayList<>();
        this.total = 0.0;
        this.totalCogs = 0.0;
        this.grossProfit = 0.0;
    }

    private boolean isValidPaymentMethod(String method) {
        if (method == null) return false;
        String m = method.trim().toUpperCase();
        return m.equals("CASH") || m.equals("QRIS") || m.equals("TRANSFER") || m.equals("OTHER");
    }

    /**
     * Menambahkan item belanja ke dalam transaksi dengan harga jual kustom.
     * Secara otomatis mengalokasikan stok FIFO dari objek Produk,
     * membentuk objek DetailTransaksi, dan memperbarui ringkasan finansial transaksi.
     */
    public DetailTransaksi tambahItem(Produk produk, int jumlah, double hargaSatuan) {
        if (!"COMPLETED".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("Tidak dapat menambahkan item ke transaksi yang sudah berstatus: " + this.status);
        }
        if (produk == null) {
            throw new IllegalArgumentException("Produk tidak boleh null.");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah item harus lebih dari 0.");
        }
        if (hargaSatuan < 0) {
            throw new IllegalArgumentException("Harga satuan tidak boleh negatif.");
        }

        // Alokasikan stok dari Produk (FIFO). Jika stok kurang, method ini melempar exception.
        double cogsItem = produk.alokasikanStokFifo(jumlah);

        // Buat objek DetailTransaksi baru
        DetailTransaksi item = new DetailTransaksi(produk, jumlah, hargaSatuan, cogsItem);
        this.daftarItem.add(item);

        // Rekalkulasi total transaksi
        this.total += item.getSubtotal();
        this.totalCogs += item.getCogs();
        this.grossProfit = this.total - this.totalCogs;

        return item;
    }

    /**
     * Overload: menambahkan item dengan harga jual default dari produk
     */
    public DetailTransaksi tambahItem(Produk produk, int jumlah) {
        if (produk == null) {
            throw new IllegalArgumentException("Produk tidak boleh null.");
        }
        return tambahItem(produk, jumlah, produk.getHargaJual());
    }

    /**
     * Membatalkan transaksi penjualan dan mengembalikan seluruh stok produk yang telah terpotong.
     */
    public void batalkanTransaksi() {
        if ("CANCELLED".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("Transaksi " + this.invoiceNumber + " sudah pernah dibatalkan sebelumnya.");
        }

        // Kembalikan stok untuk setiap item produk fisik yang ada di daftar detail transaksi
        for (DetailTransaksi item : this.daftarItem) {
            if (item.getProduk().isProductType()) {
                item.getProduk().kembalikanStok(item.getJumlah());
            }
        }

        this.status = "CANCELLED";
    }

    /**
     * Mencetak struk transaksi ke konsol dengan format yang terstruktur.
     */
    public void cetakStruk() {
        System.out.println("==========================================================================================");
        System.out.println("                        TEGAR COPYFLOW — STRUK PENJUALAN                                  ");
        System.out.println("                Jl. Raya Fotokopi & ATK No. 10 | Telp: (0328) 667890                     ");
        System.out.println("==========================================================================================");
        System.out.printf(" No. Invoice  : %-25s Tanggal : %s\n", this.invoiceNumber, this.tanggalTransaksi);
        System.out.printf(" Pembayaran   : %-25s Status  : %s\n", this.paymentMethod, this.status);
        if (this.catatan != null && !this.catatan.equals("-")) {
            System.out.printf(" Catatan      : %s\n", this.catatan);
        }
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf(" %-25s | %-10s | %-12s | %-14s | %-14s\n",
                "Nama Produk / Jasa", "Qty", "Harga Satuan", "Subtotal", "Laba Kotor");
        System.out.println("------------------------------------------------------------------------------------------");
        for (DetailTransaksi it : this.daftarItem) {
            System.out.printf(" %-25s | %3d %-6s | Rp %,9.0f | Rp %,11.0f | Rp %,11.0f\n",
                    it.getNamaProduk(),
                    it.getJumlah(),
                    it.getProduk().getSatuan(),
                    it.getHargaSatuan(),
                    it.getSubtotal(),
                    it.getGrossProfit());
        }
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf(" TOTAL PENJUALAN : Rp %,14.0f\n", this.total);
        System.out.printf(" TOTAL HPP (COGS): Rp %,14.0f\n", this.totalCogs);
        System.out.printf(" LABA KOTOR      : Rp %,14.0f (Margin: %.1f%%)\n",
                this.grossProfit, (this.total > 0 ? (this.grossProfit / this.total * 100) : 0));
        System.out.println("==========================================================================================");
    }

    // Encapsulation Getters
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public String getCatatan() {
        return catatan;
    }

    public List<DetailTransaksi> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }

    public double getTotal() {
        return total;
    }

    public double getTotalCogs() {
        return totalCogs;
    }

    public double getGrossProfit() {
        return grossProfit;
    }

    @Override
    public String toString() {
        return String.format("[Invoice: %s | Tgl: %s | Status: %s | Total: Rp %,.0f | Item: %d]",
                invoiceNumber, tanggalTransaksi, status, total, daftarItem.size());
    }
}
