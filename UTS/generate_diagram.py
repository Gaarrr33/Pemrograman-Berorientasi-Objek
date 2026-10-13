import os
from PIL import Image, ImageDraw, ImageFont
from reportlab.lib.pagesizes import landscape, A4
from reportlab.pdfgen import canvas

def generate_technical_uml_diagram():
    # Dimensi canvas PNG standar laporan teknis (resolusi tinggi untuk cetak)
    width = 2050
    height = 1350
    img = Image.new("RGB", (width, height), color="#FFFFFF")
    draw = ImageDraw.Draw(img)

    # Font standar teknis akademik (Arial / Consolas)
    try:
        font_title = ImageFont.truetype("arialbd.ttf", 24)
        font_cls_name = ImageFont.truetype("arialbd.ttf", 16)
        font_stereo = ImageFont.truetype("ariali.ttf", 13)
        font_text = ImageFont.truetype("consola.ttf", 13)
        font_rel = ImageFont.truetype("arial.ttf", 13)
        font_multi = ImageFont.truetype("arialbd.ttf", 12)
    except:
        font_title = font_cls_name = font_stereo = font_text = font_rel = font_multi = ImageFont.load_default()

    # Judul Teknis Standar Laporan (Bukan banner warna-warni)
    draw.text((width // 2, 45), "Class Diagram: Sistem Informasi Klinik Sehat Medika", fill="#000000", font=font_title, anchor="mm")
    draw.text((width // 2, 72), "Studi Kasus Tipe B - Pemrograman Berorientasi Objek (Java)", fill="#333333", font=font_rel, anchor="mm")
    draw.line([(60, 95), (width - 60, 95)], fill="#000000", width=2)

    # Helper function kotak kelas UML standar (Gaya hitam-putih / grayscale murni)
    def draw_uml_box(x, y, w, h, stereotype, class_name, attributes, methods):
        # Kotak luar
        draw.rectangle([(x, y), (x + w, y + h)], fill="#FFFFFF", outline="#000000", width=2)
        
        # Header kompartemen
        header_h = 44 if stereotype else 34
        draw.line([(x, y + header_h), (x + w, y + header_h)], fill="#000000", width=2)
        
        cur_y = y + 8
        if stereotype:
            draw.text((x + w // 2, cur_y), f"<<{stereotype}>>", fill="#000000", font=font_stereo, anchor="mt")
            cur_y += 18
        draw.text((x + w // 2, cur_y), class_name, fill="#000000", font=font_cls_name, anchor="mt")
        
        # Atribut kompartemen
        cur_y = y + header_h + 8
        if attributes:
            for attr in attributes:
                draw.text((x + 12, cur_y), attr, fill="#000000", font=font_text)
                cur_y += 18
            cur_y += 4
            draw.line([(x, cur_y), (x + w, cur_y)], fill="#000000", width=1)
            cur_y += 8
        else:
            draw.line([(x, cur_y), (x + w, cur_y)], fill="#000000", width=1)
            cur_y += 8

        # Method kompartemen
        for meth in methods:
            draw.text((x + 12, cur_y), meth, fill="#000000", font=font_text)
            cur_y += 18

    # 1. Interface PelayananMedis
    draw_uml_box(
        x=50, y=130, w=430, h=150,
        stereotype="interface", class_name="PelayananMedis",
        attributes=[],
        methods=[
            "+ {abstract} beriPelayanan(p: Pasien, k: String): void",
            "+ {abstract} getPeran(): String"
        ]
    )

    # 2. Abstract Class TenagaMedis
    draw_uml_box(
        x=530, y=130, w=430, h=310,
        stereotype="abstract", class_name="TenagaMedis",
        attributes=[
            "- idTenagaMedis : String",
            "- nama : String",
            "- spesialisasi : String"
        ],
        methods=[
            "+ TenagaMedis(id, nama, spesialisasi)",
            "+ getIdTenagaMedis() : String",
            "+ setIdTenagaMedis(id: String) : void",
            "+ getNama() : String",
            "+ setNama(nama: String) : void",
            "+ getSpesialisasi() : String",
            "+ setSpesialisasi(spe: String) : void",
            "+ {abstract} beriPelayanan(p: Pasien, k: String): void",
            "+ tampilkanProfil() : void",
            "+ toString() : String"
        ]
    )

    # 3. Class Klinik
    draw_uml_box(
        x=1040, y=130, w=450, h=360,
        stereotype=None, class_name="Klinik",
        attributes=[
            "- namaKlinik : String",
            "- alamat : String",
            "- counterAntrean : int",
            "- counterKunjungan : int"
        ],
        methods=[
            "+ Klinik(nama, alamat)",
            "+ getNamaKlinik() : String",
            "+ tambahPasien(p: Pasien) : void",
            "+ cariPasien(id: String) : Pasien",
            "+ tambahTenagaMedis(tm: TenagaMedis) : void",
            "+ cariTenagaMedis(id: String) : TenagaMedis",
            "+ daftarkanKunjungan(...) : Kunjungan",
            "+ getAntreanMenungguTerurut() : List<Kunjungan>",
            "+ prosesAntreanTerdepan() : Kunjungan",
            "+ inisialisasiDataAwal() : void",
            "+ tampilkanAntreanPrioritas() : void"
        ]
    )

    # 4. Class Main
    draw_uml_box(
        x=1560, y=130, w=430, h=250,
        stereotype=None, class_name="Main",
        attributes=[],
        methods=[
            "+ {static} main(args: String[]) : void",
            "- {static} tampilkanMenuUtama() : void",
            "- {static} menuTambahPasien(...) : void",
            "- {static} menuDaftarkanKunjungan(...) : void",
            "- {static} menuProsesPelayanan(...) : void",
            "+ {static} jalankanSkenarioPengujian(...) : void",
            "- {static} bacaInteger(scanner, prompt) : int"
        ]
    )

    # 5. Class Dokter (Subclass)
    draw_uml_box(
        x=230, y=510, w=340, h=230,
        stereotype=None, class_name="Dokter",
        attributes=[
            "- nomorSip : String",
            "- biayaKonsultasi : double"
        ],
        methods=[
            "+ Dokter(id, nama, spe, sip, biaya)",
            "+ getNomorSip() : String",
            "+ setNomorSip(sip: String) : void",
            "+ getBiayaKonsultasi() : double",
            "+ setBiayaKonsultasi(biaya: double) : void",
            "+ getPeran() : String",
            "+ beriPelayanan(p: Pasien, k: String): void"
        ]
    )

    # 6. Class Perawat (Subclass)
    draw_uml_box(
        x=610, y=510, w=340, h=230,
        stereotype=None, class_name="Perawat",
        attributes=[
            "- shiftKerja : String",
            "- ruangTugas : String"
        ],
        methods=[
            "+ Perawat(id, nama, spe, shift, ruang)",
            "+ getShiftKerja() : String",
            "+ setShiftKerja(shift: String) : void",
            "+ getRuangTugas() : String",
            "+ setRuangTugas(ruang: String) : void",
            "+ getPeran() : String",
            "+ beriPelayanan(p: Pasien, k: String): void"
        ]
    )

    # 7. Class Kunjungan
    draw_uml_box(
        x=1050, y=530, w=440, h=405,
        stereotype="Comparable<Kunjungan>", class_name="Kunjungan",
        attributes=[
            "- idKunjungan : String",
            "- nomorAntrean : int",
            "- keluhan : String",
            "- prioritas : int",
            "- status : String"
        ],
        methods=[
            "+ Kunjungan(id, no, p, tm, kel, prio)",
            "+ getIdKunjungan() : String",
            "+ getNomorAntrean() : int",
            "+ getPasien() : Pasien",
            "+ getTenagaMedis() : TenagaMedis",
            "+ getPrioritas() : int",
            "+ setPrioritas(prioritas: int) : void",
            "+ getStatus() : String",
            "+ getTeksPrioritas() : String",
            "+ prosesPelayanan() : void",
            "+ compareTo(other: Kunjungan) : int",
            "+ tampilkanBarisTabel() : void"
        ]
    )

    # 8. Class Pasien
    draw_uml_box(
        x=280, y=840, w=380, h=350,
        stereotype=None, class_name="Pasien",
        attributes=[
            "- idPasien : String",
            "- nama : String",
            "- usia : int",
            "- jenisKelamin : String",
            "- alamat : String"
        ],
        methods=[
            "+ Pasien(id, nama, usia, jk, almt)",
            "+ getIdPasien() : String",
            "+ setIdPasien(id: String) : void",
            "+ getNama() : String",
            "+ setNama(nama: String) : void",
            "+ getUsia() : int",
            "+ setUsia(usia: int) : void",
            "+ getJenisKelamin() : String",
            "+ getAlamat() : String",
            "+ tampilkanInfo() : void",
            "+ toString() : String"
        ]
    )

    # Helper garis putus-putus
    def draw_dashed_line(p1, p2, color="#000000", width=2, dash=8):
        x1, y1 = p1
        x2, y2 = p2
        dist = ((x2 - x1)**2 + (y2 - y1)**2)**0.5
        if dist == 0: return
        dx = (x2 - x1) / dist
        dy = (y2 - y1) / dist
        curr = 0
        while curr < dist:
            next_curr = min(curr + dash, dist)
            draw.line([(x1 + dx * curr, y1 + dy * curr), (x1 + dx * next_curr, y1 + dy * next_curr)], fill=color, width=width)
            curr += dash * 2

    # =========================================================================
    # RELASI UML TEKNIS RESMI (STANDARD UML CONNECTORS)
    # =========================================================================

    # 1. Realization: TenagaMedis implements PelayananMedis
    # Garis putus-putus dengan panah segitiga berongga putih
    draw_dashed_line((530, 200), (480, 200), color="#000000", width=2)
    draw.polygon([(480, 200), (496, 192), (496, 208)], fill="#FFFFFF", outline="#000000")
    draw.text((505, 180), "<<implements>>", fill="#000000", font=font_stereo, anchor="mb")

    # 2. Generalization / Inheritance: Dokter & Perawat extends TenagaMedis
    # Garis solid dengan panah segitiga berongga putih ke TenagaMedis
    draw.line([(400, 510), (400, 470)], fill="#000000", width=2)
    draw.line([(780, 510), (780, 470)], fill="#000000", width=2)
    draw.line([(400, 470), (780, 470)], fill="#000000", width=2)
    draw.line([(745, 470), (745, 440)], fill="#000000", width=2)
    draw.polygon([(745, 440), (737, 455), (753, 455)], fill="#FFFFFF", outline="#000000")
    draw.text((470, 450), "<<extends>>", fill="#000000", font=font_stereo)

    # 3. Aggregation: Klinik mengelola TenagaMedis
    # Garis solid dengan belah ketupat putih (diamond) di sisi Klinik
    draw.line([(1040, 220), (960, 220)], fill="#000000", width=2)
    draw.polygon([(1040, 220), (1030, 214), (1020, 220), (1030, 226)], fill="#FFFFFF", outline="#000000")
    draw.text((1005, 202), "1", fill="#000000", font=font_multi)
    draw.text((965, 202), "0..*", fill="#000000", font=font_multi)
    draw.text((995, 228), "memiliki", fill="#000000", font=font_rel, anchor="mt")

    # 4. Aggregation: Klinik mengelola Kunjungan
    draw.line([(1250, 490), (1250, 530)], fill="#000000", width=2)
    draw.polygon([(1250, 490), (1244, 498), (1250, 506), (1256, 498)], fill="#FFFFFF", outline="#000000")
    draw.text((1260, 492), "1", fill="#000000", font=font_multi)
    draw.text((1260, 515), "0..*", fill="#000000", font=font_multi)
    draw.text((1260, 502), "mengelola", fill="#000000", font=font_rel)

    # 5. Aggregation: Klinik mengelola Pasien
    draw.line([(1060, 490), (1060, 510)], fill="#000000", width=2)
    draw.line([(1060, 510), (1000, 510)], fill="#000000", width=2)
    draw.line([(1000, 510), (1000, 980)], fill="#000000", width=2)
    draw.line([(1000, 980), (660, 980)], fill="#000000", width=2)
    draw.polygon([(1060, 490), (1054, 498), (1060, 506), (1066, 498)], fill="#FFFFFF", outline="#000000")
    draw.text((1070, 495), "1", fill="#000000", font=font_multi)
    draw.text((670, 965), "0..*", fill="#000000", font=font_multi)
    draw.text((820, 965), "mengelola pasien", fill="#000000", font=font_rel)

    # 6. Association: Kunjungan --> Pasien
    draw.line([(1050, 890), (660, 890)], fill="#000000", width=2)
    draw.line([(660, 890), (672, 883)], fill="#000000", width=2)
    draw.line([(660, 890), (672, 897)], fill="#000000", width=2)
    draw.text((1010, 873), "0..*", fill="#000000", font=font_multi)
    draw.text((685, 873), "1", fill="#000000", font=font_multi)
    draw.text((820, 873), "pasien", fill="#000000", font=font_rel)

    # 7. Association: Kunjungan --> TenagaMedis
    draw.line([(1050, 660), (980, 660)], fill="#000000", width=2)
    draw.line([(980, 660), (980, 390)], fill="#000000", width=2)
    draw.line([(980, 390), (960, 390)], fill="#000000", width=2)
    draw.line([(960, 390), (972, 383)], fill="#000000", width=2)
    draw.line([(960, 390), (972, 397)], fill="#000000", width=2)
    draw.text((1010, 645), "0..*", fill="#000000", font=font_multi)
    draw.text((965, 372), "1", fill="#000000", font=font_multi)
    draw.text((985, 580), "tenagaMedis", fill="#000000", font=font_rel)

    # 8. Dependency: Main ..> Klinik
    draw_dashed_line((1560, 230), (1490, 230), color="#000000", width=2)
    draw.line([(1490, 230), (1502, 223)], fill="#000000", width=2)
    draw.line([(1490, 230), (1502, 237)], fill="#000000", width=2)
    draw.text((1525, 212), "<<uses>>", fill="#000000", font=font_stereo, anchor="mb")

    # Catatan / Notasi UML Standar Akademik (Sederhana di pojok kanan bawah)
    box_n_x = 1050
    box_n_y = 970
    box_n_w = 940
    box_n_h = 220
    draw.rectangle([(box_n_x, box_n_y), (box_n_x + box_n_w, box_n_y + box_n_h)], fill="#FFFFFF", outline="#000000", width=1)
    draw.text((box_n_x + 16, box_n_y + 14), "KETERANGAN NOTASI UML STANDAR:", fill="#000000", font=font_cls_name)
    
    uml_notes = [
        "- Garis putus-putus dengan panah segitiga berongga: Realisasi Interface (TenagaMedis mengimplementasikan PelayananMedis).",
        "- Garis solid dengan panah segitiga berongga: Pewarisan/Inheritance (Dokter dan Perawat merupakan turunan TenagaMedis).",
        "- Garis solid dengan belah ketupat putih: Relasi Agregasi (Klinik mengelola objek Pasien, TenagaMedis, dan Kunjungan).",
        "- Garis solid dengan panah terbuka: Relasi Asosiasi berarah (Kunjungan berelasi langsung dengan Pasien dan TenagaMedis).",
        "- Simbol visibilitas: (-) menunjukkan atribut private; (+) menunjukkan method public.",
        "- Multiplicity: [1] bernilai satu objek; [0..*] bernilai banyak objek (koleksi/list)."
    ]
    cur_y = box_n_y + 44
    for note in uml_notes:
        draw.text((box_n_x + 16, cur_y), note, fill="#000000", font=font_rel)
        cur_y += 26

    # Simpan PNG
    png_path = r"d:\TUGAS KULIAH\Semester 3\Pemrograman Berorientasi Objek\UTS-Praktikum-PBO\UTS_TIPE_B\class-diagram.png"
    img.save(png_path, "PNG", dpi=(150, 150))
    print(f"[SUKSES] Technical PNG disimpan di: {png_path}")

    # Simpan PDF
    pdf_path = r"d:\TUGAS KULIAH\Semester 3\Pemrograman Berorientasi Objek\UTS-Praktikum-PBO\UTS_TIPE_B\class-diagram.pdf"
    page_w, page_h = landscape(A4)
    c = canvas.Canvas(pdf_path, pagesize=(page_w, page_h))
    c.setTitle("Class Diagram - Sistem Informasi Klinik Sehat Medika")
    c.setAuthor("Mahasiswa D3 Teknik Informatika")
    
    margin = 15
    draw_w = page_w - (2 * margin)
    draw_h = page_h - (2 * margin)
    
    c.drawImage(png_path, margin, margin, width=draw_w, height=draw_h, preserveAspectRatio=True)
    c.showPage()
    c.save()
    print(f"[SUKSES] Technical PDF disimpan di: {pdf_path}")

if __name__ == "__main__":
    generate_technical_uml_diagram()
