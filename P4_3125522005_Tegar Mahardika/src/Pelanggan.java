public class Pelanggan {

    private String idPelanggan;
    private String nama;
    private String noTelepon;

    public Pelanggan(String idPelanggan, String nama, String noTelepon) {
        setIdPelanggan(idPelanggan);
        setNama(nama);
        setNoTelepon(noTelepon);
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        if (idPelanggan == null || idPelanggan.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "ID pelanggan tidak boleh kosong."
            );
        }
        this.idPelanggan = idPelanggan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Nama pelanggan tidak boleh kosong."
            );
        }
        this.nama = nama;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        if (noTelepon == null || noTelepon.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Nomor telepon tidak boleh kosong."
            );
        }

        if (!noTelepon.matches("\\d+")) {
            throw new IllegalArgumentException(
                "Nomor telepon hanya boleh berisi angka."
            );
        }

        this.noTelepon = noTelepon;
    }

    public void ubahNama(String namaBaru) {
        setNama(namaBaru);
    }

    public void tampilkanNama() {
        System.out.println("Nama Pelanggan : " + nama);
    }
}