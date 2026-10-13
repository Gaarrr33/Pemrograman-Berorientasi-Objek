import os
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def set_cell_margins(cell, top=80, bottom=80, left=100, right=100):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = OxmlElement('w:tcMar')
    for m, val in [('top', top), ('bottom', bottom), ('left', left), ('right', right)]:
        node = OxmlElement(f'w:{m}')
        node.set(qn('w:w'), str(val))
        node.set(qn('w:type'), 'dxa')
        tcMar.append(node)
    tcPr.append(tcMar)

def set_cell_background(cell, color_hex):
    shading_xml = f'<w:shd {nsdecls("w")} w:fill="{color_hex}"/>'
    cell._tc.get_or_add_tcPr().append(parse_xml(shading_xml))

def set_table_borders(table, color="B0B0B0"):
    tblPr = table._tbl.tblPr
    borders_xml = f'''
    <w:tblBorders {nsdecls("w")}>
        <w:top w:val="single" w:sz="6" w:space="0" w:color="{color}"/>
        <w:bottom w:val="single" w:sz="6" w:space="0" w:color="{color}"/>
        <w:insideH w:val="single" w:sz="4" w:space="0" w:color="{color}"/>
        <w:insideV w:val="single" w:sz="4" w:space="0" w:color="{color}"/>
        <w:left w:val="single" w:sz="6" w:space="0" w:color="{color}"/>
        <w:right w:val="single" w:sz="6" w:space="0" w:color="{color}"/>
    </w:tblBorders>
    '''
    tblPr.append(parse_xml(borders_xml))

def build_practicum_report():
    doc = docx.Document()

    # Margin 1 inch
    for section in doc.sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)

    # Base style
    style = doc.styles['Normal']
    font = style.font
    font.name = 'Times New Roman'
    font.size = Pt(11.5)
    font.color.rgb = RGBColor(0, 0, 0)
    style.paragraph_format.line_spacing = 1.3
    style.paragraph_format.space_after = Pt(4)

    def add_title(text):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(12)
        r = p.add_run(text)
        r.bold = True
        r.font.size = Pt(14)
        return p

    def add_section_header(num_and_title):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after = Pt(3)
        r = p.add_run(num_and_title)
        r.bold = True
        r.font.size = Pt(12)
        return p

    def add_p(text, bold_prefix=None):
        p = doc.add_paragraph()
        p.paragraph_format.space_after = Pt(4)
        p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        if bold_prefix:
            rb = p.add_run(bold_prefix)
            rb.bold = True
        p.add_run(text)
        return p

    # JUDUL DOKUMEN
    add_title("LAPORAN HASIL PRAKTIKUM UTS\nPEMROGRAMAN BERORIENTASI OBJEK (PBO)")

    # 1. IDENTITAS MAHASISWA
    add_section_header("1. Identitas Mahasiswa")
    t_id = doc.add_table(rows=4, cols=2)
    t_id.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_id)
    id_data = [
        ("Nama Mahasiswa", ": [Nama Mahasiswa]"),
        ("NRP", ": [NRP Mahasiswa]"),
        ("Kelas", ": [Kelas]"),
        ("Mata Kuliah", ": Praktikum Pemrograman Berorientasi Objek")
    ]
    for idx, (label, val) in enumerate(id_data):
        row = t_id.rows[idx].cells
        row[0].paragraphs[0].add_run(label).bold = True
        row[1].paragraphs[0].add_run(val)
        set_cell_margins(row[0], top=40, bottom=40, left=80, right=80)
        set_cell_margins(row[1], top=40, bottom=40, left=80, right=80)
        row[0].width = Inches(2.0)
        row[1].width = Inches(4.5)

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # 2. STUDI KASUS
    add_section_header("2. Studi Kasus")
    add_p(
        "Studi kasus yang dipilih adalah Sistem Klinik (Tipe B). Tujuan dari program ini adalah membangun aplikasi "
        "pengelolaan klinik berbasis console (CLI) menggunakan Java standar untuk mencatat data pasien, tenaga medis "
        "(dokter dan perawat), registrasi kunjungan pasien, serta pemrosesan antrean pelayanan medis. Program dirancang "
        "sederhana, mudah dijelaskan oleh mahasiswa D3 Teknik Informatika, namun memenuhi seluruh kaidah Pemrograman "
        "Berorientasi Objek dan aturan bisnis klinik secara nyata."
    )

    # 3. RANCANGAN CLASS
    add_section_header("3. Rancangan Class dan Relasi")
    add_p(
        "Program terdiri dari 7 class dan 1 interface yang saling berelasi. Setiap class memiliki tanggung jawab tunggal "
        "(single responsibility) sebagaimana dirangkum pada tabel berikut:"
    )

    t_cls = doc.add_table(rows=1, cols=3)
    t_cls.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_cls)
    chdr = t_cls.rows[0].cells
    chdr[0].paragraphs[0].add_run("Nama Class / File").bold = True
    chdr[1].paragraphs[0].add_run("Tipe").bold = True
    chdr[2].paragraphs[0].add_run("Fungsi dan Tanggung Jawab").bold = True
    for c in chdr:
        set_cell_background(c, "F2F2F2")
        set_cell_margins(c, top=60, bottom=60, left=80, right=80)

    class_list = [
        ("PelayananMedis", "Interface", "Mendefinisikan kontrak method beriPelayanan() dan getPeran() yang wajib diimplementasikan tenaga medis."),
        ("TenagaMedis", "Abstract Class", "Superclass yang memuat data umum (id, nama, spesialisasi) dan abstract method beriPelayanan()."),
        ("Dokter", "Subclass", "Turunan TenagaMedis; menangani pemeriksaan fisik, diagnosis klinis, resep obat, dan biaya SIP."),
        ("Perawat", "Subclass", "Turunan TenagaMedis; menangani triase, pengukuran tanda vital (tensi/suhu), dan asuhan keperawatan."),
        ("Pasien", "Class", "Menyimpan data identitas pasien (id, nama, usia, jenis kelamin, alamat) dengan enkapsulasi dan validasi."),
        ("Kunjungan", "Class", "Menghubungkan Pasien dengan TenagaMedis; menyimpan keluhan, tingkat urgensi (prioritas), dan status kunjungan."),
        ("Klinik", "Class", "Pengelola utama (agregasi) daftar pasien, tenaga medis, dan antrean kunjungan pasien."),
        ("Main", "Class", "Entry point program; menyediakan menu console interaktif (Scanner) dan pengujian otomatis.")
    ]
    for cname, ctype, cdesc in class_list:
        row = t_cls.add_row().cells
        row[0].paragraphs[0].add_run(cname)
        row[1].paragraphs[0].add_run(ctype)
        row[2].paragraphs[0].add_run(cdesc)
        for c in row:
            set_cell_margins(c, top=40, bottom=40, left=80, right=80)

    add_p(
        "Hubungan antarclass secara ringkas: Klinik memiliki relasi Agregasi terhadap Pasien, TenagaMedis, dan Kunjungan. "
        "Kunjungan memiliki relasi Asosiasi langsung dengan Pasien (1 objek) dan TenagaMedis (1 objek). Dokter dan Perawat "
        "menerapkan relasi Inheritance (pewarisan) dari TenagaMedis. TenagaMedis mengimplementasikan interface PelayananMedis, "
        "dan Kunjungan mengimplementasikan interface Comparable<Kunjungan>."
    )

    # 4. PENERAPAN KONSEP OOP
    add_section_header("4. Penerapan Konsep Pemrograman Berorientasi Objek (OOP)")
    add_p(
        "Seluruh konsep OOP diterapkan secara nyata pada source code program:",
        bold_prefix="Keterangan Implementasi: "
    )
    add_p(
        "Diterapkan melalui pembagian entitas program ke dalam 7 class dan 1 interface. Objek konkret diinstansiasi "
        "pada runtime, seperti pembuatan objek Dokter (dr. Budi Santoso, Sp.PD), Perawat (Ners Andi Pratama), dan beberapa objek Pasien.",
        bold_prefix="• Class dan Object: "
    )
    add_p(
        "Seluruh atribut pada class Pasien, TenagaMedis, Dokter, Perawat, Kunjungan, dan Klinik dideklarasikan dengan "
        "access modifier private. Akses terhadap nilai atribut dilakukan melalui method getter, setter, dan constructor. "
        "Tidak ada atribut yang dapat diakses atau diubah langsung dari luar class.",
        bold_prefix="• Encapsulation: "
    )
    add_p(
        "Diterapkan minimal 4 aturan bisnis aktif: (1) Nama pasien tidak boleh kosong/blank; (2) Usia pasien harus > 0 tahun; "
        "(3) ID pasien tidak boleh didaftarkan duplikat di Klinik (melempar IllegalArgumentException); dan (4) Kunjungan yang telah "
        "berstatus SELESAI ditolak jika diproses ulang (melempar IllegalStateException).",
        bold_prefix="• Validation dan Business Rule: "
    )
    add_p(
        "Klinik memiliki relasi Agregasi dengan mengelola koleksi List<Pasien>, List<TenagaMedis>, dan List<Kunjungan>. "
        "Kunjungan berasosiasi langsung dengan tepat satu Pasien dan satu TenagaMedis.",
        bold_prefix="• Relationship Antarobject: "
    )
    add_p(
        "Class TenagaMedis bertindak sebagai superclass bagi subclass Dokter dan Perawat menggunakan kata kunci extends. "
        "Dokter dan Perawat mewarisi atribut idTenagaMedis, nama, dan spesialisasi serta method getProfil().",
        bold_prefix="• Inheritance: "
    )
    add_p(
        "TenagaMedis dideklarasikan sebagai abstract class karena profesi tenaga medis harus spesifik saat melayani pasien. "
        "Class ini memiliki abstract method: public abstract void beriPelayanan(Pasien pasien, String keluhan); yang wajib "
        "didefinisikan ulang oleh setiap subclass.",
        bold_prefix="• Abstract Class: "
    )
    add_p(
        "Interface PelayananMedis mendefinisikan kontrak pelayanan (beriPelayanan dan getPeran) yang diimplementasikan "
        "oleh TenagaMedis. Selain itu, interface Comparable<Kunjungan> diimplementasikan pada Kunjungan untuk mendukung sorting antrean.",
        bold_prefix="• Interface: "
    )
    add_p(
        "Method beriPelayanan() dioverride berbeda pada Dokter (pemeriksaan fisik, diagnosis klinis, resep obat, tarif konsultasi) "
        "dan Perawat (triase awal, pemeriksaan tensi/suhu, pertolongan pertama). Polimorfisme dibuktikan pada pemanggilan: "
        "tenagaMedis.beriPelayanan(pasien, keluhan); di mana pemanggilan melalui referensi bertipe superclass TenagaMedis "
        "menghasilkan output yang berbeda sesuai objek konkret pada runtime.",
        bold_prefix="• Polymorphism dan Method Overriding: "
    )

    # 5. FITUR KREATIF
    add_section_header("5. Fitur Kreatif: Sistem Antrean Triase Berdasarkan Tingkat Urgensi")
    add_p(
        "Fitur kreatif yang ditambahkan adalah sistem prioritas antrean pasien multi-level berdasarkan tingkat kegawatdaruratan (triase medis):\n"
        "- Prioritas 1 (Darurat / Emergency): Kondisi gawat darurat yang mengancam jiwa (misal nyeri dada akut, sesak napas).\n"
        "- Prioritas 2 (Mendesak / Urgent): Kondisi sakit akut namun stabil (misal demam tinggi berhari-hari, batuk rejan).\n"
        "- Prioritas 3 (Reguler / Normal): Pemeriksaan berkala, kontrol rutin, atau keluhan ringan.\n\n"
        "Cara Kerja: Kunjungan mengimplementasikan interface Comparable<Kunjungan>. Pada method compareTo, sistem membandingkan "
        "nilai prioritas terlebih dahulu (angka 1 mendahului 2, 2 mendahului 3). Jika prioritasnya sama, sistem membandingkan nomor "
        "antrean pendaftaran (FIFO). Manfaatnya, pasien darurat yang mendaftar belakangan secara otomatis melompati antrean reguler "
        "sehingga penanganan darurat dapat diberikan tepat waktu tanpa intervensi manual yang subjektif."
    )

    # 6. CLASS DIAGRAM
    add_section_header("6. Class Diagram")
    add_p("Class diagram teknis standar UML yang sesuai dengan implementasi akhir source code:")

    png_path = r"d:\TUGAS KULIAH\Semester 3\Pemrograman Berorientasi Objek\UTS-Praktikum-PBO\UTS_TIPE_B\class-diagram.png"
    if os.path.exists(png_path):
        p_img = doc.add_paragraph()
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(4)
        p_img.paragraph_format.space_after = Pt(2)
        r_img = p_img.add_run()
        r_img.add_picture(png_path, width=Inches(6.2))

        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_after = Pt(8)
        r_cap = p_cap.add_run("Gambar 1. Class Diagram Teknis Sistem Informasi Klinik Sehat Medika")
        r_cap.font.size = Pt(9.5)
        r_cap.italic = True

    # 7. PENGUJIAN PROGRAM
    add_section_header("7. Pengujian Program")
    add_p("Tabel hasil pengujian faktual yang dijalankan langsung terhadap program:")

    t_test = doc.add_table(rows=1, cols=4)
    t_test.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_table_borders(t_test)
    thdr = t_test.rows[0].cells
    thdr[0].paragraphs[0].add_run("No").bold = True
    thdr[1].paragraphs[0].add_run("Skenario Pengujian").bold = True
    thdr[2].paragraphs[0].add_run("Hasil yang Diharapkan").bold = True
    thdr[3].paragraphs[0].add_run("Hasil Aktual & Status").bold = True
    for c in thdr:
        set_cell_background(c, "F2F2F2")
        set_cell_margins(c, top=60, bottom=60, left=80, right=80)

    test_rows = [
        ("1", "Validasi Nama Pasien Kosong (Input: '   ')", "Sistem menolak dan melempar exception.", "Ditolak: 'Nama pasien tidak boleh kosong!' (Lulus)"),
        ("2", "Validasi Usia Pasien Negatif (Input: -5)", "Sistem menolak dan melempar exception.", "Ditolak: 'Usia pasien harus lebih besar dari 0 tahun!' (Lulus)"),
        ("3", "Pencegahan Duplikasi ID Pasien ('P001')", "Pendaftaran ditolak karena ID sudah terdaftar.", "Ditolak: 'Pasien dengan ID [P001] sudah terdaftar di klinik!' (Lulus)"),
        ("4", "Fitur Antrean Prioritas (P1 Darurat vs P3 Reguler)", "Pasien darurat melompati antrean menjadi urutan pertama.", "KJ003 (#3, Prioritas 1) diposisikan di urutan terdepan dan dilayani pertama. (Lulus)"),
        ("5", "Polimorfisme: Pelayanan Perawat", "Mengeksekusi asuhan keperawatan dan triase.", "Output: [TINDAKAN ASUHAN KEPERAWATAN] Triase, Tensi, Suhu, Stabilisasi. (Lulus)"),
        ("6", "Polimorfisme: Pelayanan Dokter", "Mengeksekusi pemeriksaan diagnosis dan resep obat.", "Output: [TINDAKAN MEDIS DOKTER] Diagnosis klinis, Resep obat, SIP, Biaya. (Lulus)"),
        ("7", "Validasi Status Kunjungan Selesai", "Menolak pemrosesan ulang pada kunjungan selesai.", "Ditolak: 'Kunjungan [KJ003] sudah berstatus SELESAI...' (Lulus)")
    ]

    for no, scen, exp, act in test_rows:
        row = t_test.add_row().cells
        row[0].paragraphs[0].add_run(no)
        row[1].paragraphs[0].add_run(scen)
        row[2].paragraphs[0].add_run(exp)
        row[3].paragraphs[0].add_run(act)
        for c in row:
            set_cell_margins(c, top=40, bottom=40, left=80, right=80)

    add_p("Tangkapan layar (log teks) terminal aktual yang membuktikan program benar-benar berjalan:")

    p_log = doc.add_paragraph()
    p_log.paragraph_format.space_before = Pt(4)
    p_log.paragraph_format.space_after = Pt(8)
    log_text = (
        "[UJI 1] PEMBUKTIAN VALIDASI & BUSINESS RULES\n"
        "-> Uji 1: Nama kosong -> Ditolak: Nama pasien tidak boleh kosong!\n"
        "-> Uji 2: Usia negatif -> Ditolak: Usia pasien harus lebih besar dari 0 tahun!\n"
        "-> Uji 3: ID duplikat  -> Ditolak: Pasien dengan ID [P001] sudah terdaftar di klinik!\n\n"
        "[UJI 2] PEMBUKTIAN FITUR KREATIF: PENGURUTAN ANTREAN PRIORITAS\n"
        "+--------+-------+--------------+-------------------+-----------------------------+-----------+\n"
        "| ID     | Antre | Prioritas    | Nama Pasien       | Tenaga Medis                | Status    |\n"
        "+--------+-------+--------------+-------------------+-----------------------------+-----------+\n"
        "| KJ003  | #3    | 1 - DARURAT  | Bambang Pamungkas | Ners Andi Pratama, S.Kep    | MENUNGGU  |\n"
        "| KJ002  | #2    | 2 - MENDESAK | Dewi Sartika      | Ners Maya Safitri, A.Md.Kep | MENUNGGU  |\n"
        "| KJ004  | #4    | 2 - MENDESAK | Clara Amanda      | dr. Siti Rahma, Sp.A        | MENUNGGU  |\n"
        "| KJ001  | #1    | 3 - REGULER  | Ahmad Fauzi       | dr. Budi Santoso, Sp.PD     | MENUNGGU  |\n"
        "+--------+-------+--------------+-------------------+-----------------------------+-----------+\n\n"
        "[UJI 3] PEMBUKTIAN POLYMORPHISM & INTERFACE\n"
        "-> Melayani Antrean KJ003 (PERAWAT): [TINDAKAN ASUHAN KEPERAWATAN] Triase, Tensi, Suhu, Stabilisasi.\n"
        "-> Melayani Antrean KJ004 (DOKTER) : [TINDAKAN MEDIS DOKTER] Diagnosis fisik, Resep obat, SIP, Biaya.\n\n"
        "[UJI 4] PEMBUKTIAN VALIDASI STATUS KUNJUNGAN\n"
        "-> Proses ulang KJ003 (SELESAI) -> Ditolak: Kunjungan [KJ003] sudah berstatus SELESAI!"
    )
    r_l = p_log.add_run(log_text)
    r_l.font.name = "Consolas"
    r_l.font.size = Pt(8.5)

    # 8. CARA MENJALANKAN PROGRAM
    add_section_header("8. Cara Menjalankan Program")
    add_p(
        "Buka terminal pada direktori src project dan jalankan perintah berikut:\n"
        "1. Perintah Kompilasi:\n"
        "   javac *.java\n"
        "2. Perintah Menjalankan Program (Menu Interaktif):\n"
        "   java Main\n"
        "3. Perintah Menjalankan Pengujian Otomatis (Demo Nilai Mandiri):\n"
        "   java Main --demo"
    )

    docx_path = r"d:\TUGAS KULIAH\Semester 3\Pemrograman Berorientasi Objek\UTS-Praktikum-PBO\UTS_TIPE_B\Laporan_UTS_PBO_Tipe_B.docx"
    doc.save(docx_path)
    print(f"[SUKSES] Laporan DOCX berhasil diperbarui: {docx_path}")

if __name__ == "__main__":
    build_practicum_report()
