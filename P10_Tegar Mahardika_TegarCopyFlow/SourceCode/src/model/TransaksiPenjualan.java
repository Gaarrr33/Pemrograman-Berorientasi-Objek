package model;

import exception.StokTidakCukupException;
import exception.TransaksiInvalidException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class TransaksiPenjualan merepresentasikan transaksi kasir (POS)
 * pada sistem Tegar CopyFlow (tabel sales dan logika sales.js).
 * Memiliki relasi Composition dengan DetailTransaksi.
 * 
 * Mengendalikan integritas siklus transaksi:
 * - Menolak mutasi jika status transaksi sudah CANCELLED (melempar TransaksiInvalidException).
 * - Meneruskan StokTidakCukupException jika stok inventaris tidak mencukupi saat tambah item.
 * - Menjamin konsistensi state finansial dan rollback stok saat pembatalan.
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
            throw new IllegalArgumentException("Nomor invoice transaksi tidak boleh kosong.");
        }
        if (!isValidPaymentMethod(paymentMethod)) {
            throw new IllegalArgumentException("Metode pembayaran '" + paymentMethod + "' tidak valid. Pilihan: Cash, QRIS, Transfer, Other.");
        }

        this.invoiceNumber = invoiceNumber.trim();
        this.tanggalTransaksi = (tanggalTransaksi != null && !tanggalTransaksi.trim().isEmpty())
                ? tanggalTransaksi.trim()
                : "2026-11-02 10:00:00";
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
     * Mengalokasikan stok FIFO dari Produk terlebih dahulu.
     * Jika stok kurang, alokasikanStokFifo melempar StokTidakCukupException sebelum state transaksi berubah.
     */
    public DetailTransaksi tambahItem(Produk produk, int jumlah, double hargaSatuan)
            throws StokTidakCukupException, TransaksiInvalidException {
        if (!"COMPLETED".equalsIgnoreCase(this.status)) {
            throw new TransaksiInvalidException(this.invoiceNumber, this.status,
                    "Tidak dapat menambahkan item ke transaksi yang sudah berstatus: " + this.status);
        }
        if (produk == null) {
            throw new IllegalArgumentException("Objek produk tidak boleh null.");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah item pembelian harus lebih dari 0.");
        }
        if (hargaSatuan < 0) {
            throw new IllegalArgumentException("Harga satuan tidak boleh bernilai negatif.");
        }

        // Alokasikan stok dari Produk (FIFO). Melempar StokTidakCukupException jika gagal.
        double cogsItem = produk.alokasikanStokFifo(jumlah);

        // State transaksi HANYA diperbarui jika alokasi stok berhasil
        DetailTransaksi item = new DetailTransaksi(produk, jumlah, hargaSatuan, cogsItem);
        this.daftarItem.add(item);

        this.total += item.getSubtotal();
        this.totalCogs += item.getCogs();
        this.grossProfit = this.total - this.totalCogs;

        return item;
    }

    /**
     * Overload: menambahkan item dengan harga jual default dari produk
     */
    public DetailTransaksi tambahItem(Produk produk, int jumlah)
            throws StokTidakCukupException, TransaksiInvalidException {
        if (produk == null) {
            throw new IllegalArgumentException("Objek produk tidak boleh null.");
        }
        return tambahItem(produk, jumlah, produk.getHargaJual());
    }

    /**
     * Membatalkan transaksi penjualan dan mengembalikan seluruh stok produk ke batch FIFO asalnya.
     * @throws TransaksiInvalidException jika transaksi sudah pernah dibatalkan sebelumnya
     */
    public void batalkanTransaksi() throws TransaksiInvalidException {
        if ("CANCELLED".equalsIgnoreCase(this.status)) {
            throw new TransaksiInvalidException(this.invoiceNumber, this.status,
                    "Transaksi ini sudah dibatalkan sebelumnya (pembatalan ganda ditolak).");
        }

        // Kembalikan setiap item stok fisik ke batch produk asalnya
        for (DetailTransaksi item : this.daftarItem) {
            item.getProduk().kembalikanStok(item.getJumlah());
        }

        this.status = "CANCELLED";
    }

    /**
     * Mencetak struk belanja kasir dengan format tabel ASCII rapi.
     */
    public void cetakStruk() {
        System.out.println("==========================================================================================");
        System.out.println("                        TEGAR COPYFLOW — STRUK PENJUALAN                                  ");
        System.out.println("                Jl. Raya Fotokopi & ATK No. 10 | Telp: (0328) 667890                     ");
        System.out.println("==========================================================================================");
        System.out.printf(" No. Invoice  : %-25s Tanggal : %s\n", invoiceNumber, tanggalTransaksi);
        System.out.printf(" Pembayaran   : %-25s Status  : %s\n", paymentMethod, status);
        System.out.printf(" Catatan      : %s\n", catatan);
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf(" %-25s | %-10s | %-12s | %-14s | %-12s\n",
                "Nama Produk / Jasa", "Qty", "Harga Satuan", "Subtotal", "Laba Kotor");
        System.out.println("------------------------------------------------------------------------------------------");

        for (DetailTransaksi item : daftarItem) {
            System.out.println(" " + item);
        }

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf(" TOTAL PENJUALAN : Rp %,14.0f\n", total);
        System.out.printf(" TOTAL HPP (COGS): Rp %,14.0f\n", totalCogs);
        double margin = (total > 0) ? (grossProfit / total * 100.0) : 0.0;
        System.out.printf(" LABA KOTOR      : Rp %,14.0f (Margin: %.1f%%)\n", grossProfit, margin);
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
}

