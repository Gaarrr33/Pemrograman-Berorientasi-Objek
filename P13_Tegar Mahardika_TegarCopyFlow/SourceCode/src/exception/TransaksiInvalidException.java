package exception;

/**
 * Custom Checked Exception: TransaksiInvalidException
 * Dilempar ketika terjadi pelanggaran siklus transaksi kasir, seperti:
 * - Menambahkan item belanja ke transaksi yang sudah CANCELLED.
 * - Melakukan pembatalan ganda (double rollback) pada transaksi yang sudah dibatalkan.
 */
public class TransaksiInvalidException extends Exception {
    private final String invoiceNumber;
    private final String statusSaatIni;

    public TransaksiInvalidException(String invoiceNumber, String statusSaatIni, String pesan) {
        super(String.format("[Invoice: %s | Status: %s] %s", invoiceNumber, statusSaatIni, pesan));
        this.invoiceNumber = invoiceNumber;
        this.statusSaatIni = statusSaatIni;
    }

    public TransaksiInvalidException(String pesan) {
        super(pesan);
        this.invoiceNumber = "-";
        this.statusSaatIni = "-";
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getStatusSaatIni() {
        return statusSaatIni;
    }
}

