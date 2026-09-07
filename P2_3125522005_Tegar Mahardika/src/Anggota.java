public class Anggota {
    String id;
    String nama;
    String alamat;

    public Anggota(String id, String nama, String alamat) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
    }

    public void tampilkanData() {
        System.out.println("ID     : " + id);
        System.out.println("Nama   : " + nama);
        System.out.println("Alamat : " + alamat);
    }

    public void ubahAlamat(String alamatBaru) {
        alamat = alamatBaru;
    }

    public String getNama() {
        return nama;
    }
} 
