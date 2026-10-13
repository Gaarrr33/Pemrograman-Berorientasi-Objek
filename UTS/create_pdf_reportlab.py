import os
from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, PageBreak
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(NumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_number(num_pages)
            canvas.Canvas.showPage(self)
        canvas.Canvas.save(self)

    def draw_page_number(self, page_count):
        self.saveState()
        self.setFont("Times-Roman", 10)
        text = f"Halaman {self._pageNumber} dari {page_count}"
        self.drawRightString(A4[0] - 54, 36, text)
        self.restoreState()

def build_practicum_pdf_report():
    pdf_path = r"d:\TUGAS KULIAH\Semester 3\Pemrograman Berorientasi Objek\UTS-Praktikum-PBO\UTS_TIPE_B\Laporan_UTS_PBO_Tipe_B.pdf"
    doc = SimpleDocTemplate(
        pdf_path,
        pagesize=A4,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()

    style_title = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Times-Bold',
        fontSize=14,
        leading=18,
        alignment=TA_CENTER,
        spaceAfter=14
    )
    style_sec_head = ParagraphStyle(
        'SecHead',
        parent=styles['Normal'],
        fontName='Times-Bold',
        fontSize=12,
        leading=16,
        spaceBefore=10,
        spaceAfter=4
    )
    style_body = ParagraphStyle(
        'BodyCustom',
        parent=styles['Normal'],
        fontName='Times-Roman',
        fontSize=10.5,
        leading=15,
        alignment=TA_JUSTIFY,
        spaceAfter=5
    )
    style_caption = ParagraphStyle(
        'Caption',
        parent=styles['Normal'],
        fontName='Times-Italic',
        fontSize=9.5,
        leading=12,
        alignment=TA_CENTER,
        spaceBefore=4,
        spaceAfter=8
    )
    style_code = ParagraphStyle(
        'CodeStyle',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=8,
        leading=10.5
    )
    style_tbl_head = ParagraphStyle(
        'TblHead',
        parent=styles['Normal'],
        fontName='Times-Bold',
        fontSize=9.5,
        leading=12,
        alignment=TA_CENTER
    )
    style_tbl_cell = ParagraphStyle(
        'TblCell',
        parent=styles['Normal'],
        fontName='Times-Roman',
        fontSize=9,
        leading=12
    )

    story = []

    # JUDUL DOKUMEN
    story.append(Paragraph("LAPORAN HASIL PRAKTIKUM UTS<br/>PEMROGRAMAN BERORIENTASI OBJEK (PBO)", style_title))

    # 1. IDENTITAS MAHASISWA
    story.append(Paragraph("1. Identitas Mahasiswa", style_sec_head))
    id_table_data = [
        [Paragraph("Nama Mahasiswa", style_tbl_head), Paragraph(": [Nama Mahasiswa]", style_tbl_cell)],
        [Paragraph("NRP", style_tbl_head), Paragraph(": [NRP Mahasiswa]", style_tbl_cell)],
        [Paragraph("Kelas", style_tbl_head), Paragraph(": [Kelas]", style_tbl_cell)],
        [Paragraph("Mata Kuliah", style_tbl_head), Paragraph(": Praktikum Pemrograman Berorientasi Objek (PBO)", style_tbl_cell)]
    ]
    t_id = Table(id_table_data, colWidths=[1.8*inch, 5.0*inch])
    t_id.setStyle(TableStyle([
        ('BOX', (0,0), (-1,-1), 1, colors.black),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor('#CCCCCC')),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('LEFTPADDING', (0,0), (-1,-1), 6),
        ('RIGHTPADDING', (0,0), (-1,-1), 6),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE')
    ]))
    story.append(t_id)
    story.append(Spacer(1, 6))

    # 2. STUDI KASUS
    story.append(Paragraph("2. Studi Kasus", style_sec_head))
    story.append(Paragraph(
        "Studi kasus yang dipilih adalah <strong>Sistem Klinik (Tipe B)</strong>. Tujuan pembuatan program adalah membangun "
        "aplikasi pengelolaan klinik berbasis console (CLI) menggunakan Java standar untuk mengelola data pasien, tenaga medis "
        "(dokter dan perawat), pendaftaran kunjungan, serta pemrosesan pelayanan medis. Program dibuat sederhana, bersih, "
        "mudah dijelaskan oleh mahasiswa D3 Teknik Informatika, dan memenuhi seluruh persyaratan OOP serta aturan bisnis secara nyata.",
        style_body
    ))

    # 3. RANCANGAN CLASS
    story.append(Paragraph("3. Rancangan Class dan Hubungan Antarclass", style_sec_head))
    story.append(Paragraph(
        "Program terdiri dari 7 class dan 1 interface yang memiliki tanggung jawab terpisah secara modular:",
        style_body
    ))

    class_table_data = [
        [Paragraph("Nama Class / File", style_tbl_head), Paragraph("Tipe", style_tbl_head), Paragraph("Fungsi dan Tanggung Jawab", style_tbl_head)],
        [Paragraph("PelayananMedis", style_tbl_cell), Paragraph("Interface", style_tbl_cell), Paragraph("Mendefinisikan kontrak method beriPelayanan() dan getPeran() bagi tenaga kesehatan.", style_tbl_cell)],
        [Paragraph("TenagaMedis", style_tbl_cell), Paragraph("Abstract Class", style_tbl_cell), Paragraph("Superclass yang memuat data umum (id, nama, spesialisasi) dan abstract method beriPelayanan().", style_tbl_cell)],
        [Paragraph("Dokter", style_tbl_cell), Paragraph("Subclass", style_tbl_cell), Paragraph("Mewarisi TenagaMedis; menangani pemeriksaan fisik, diagnosis klinis, resep obat, dan biaya SIP.", style_tbl_cell)],
        [Paragraph("Perawat", style_tbl_cell), Paragraph("Subclass", style_tbl_cell), Paragraph("Mewarisi TenagaMedis; menangani triase, pengukuran tanda vital (tensi/suhu), dan asuhan keperawatan.", style_tbl_cell)],
        [Paragraph("Pasien", style_tbl_cell), Paragraph("Class", style_tbl_cell), Paragraph("Menyimpan data identitas pasien (id, nama, usia, jenis kelamin, alamat) dengan enkapsulasi dan validasi.", style_tbl_cell)],
        [Paragraph("Kunjungan", style_tbl_cell), Paragraph("Class", style_tbl_cell), Paragraph("Menghubungkan Pasien dengan TenagaMedis; memuat keluhan, prioritas urgensi, dan status kunjungan.", style_tbl_cell)],
        [Paragraph("Klinik", style_tbl_cell), Paragraph("Class", style_tbl_cell), Paragraph("Pengelola utama (agregasi) daftar pasien, tenaga medis, dan antrean kunjungan pasien.", style_tbl_cell)],
        [Paragraph("Main", style_tbl_cell), Paragraph("Class", style_tbl_cell), Paragraph("Entry point program; menyediakan menu console interaktif (Scanner) dan pengujian otomatis.", style_tbl_cell)]
    ]
    t_cls = Table(class_table_data, colWidths=[1.3*inch, 1.0*inch, 4.5*inch])
    t_cls.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#F2F2F2')),
        ('BOX', (0,0), (-1,-1), 1, colors.black),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor('#CCCCCC')),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('LEFTPADDING', (0,0), (-1,-1), 5),
        ('RIGHTPADDING', (0,0), (-1,-1), 5),
        ('VALIGN', (0,0), (-1,-1), 'TOP')
    ]))
    story.append(t_cls)
    story.append(Spacer(1, 4))
    story.append(Paragraph(
        "<strong>Hubungan Antarclass:</strong> Class Klinik memiliki relasi Agregasi terhadap Pasien, TenagaMedis, dan Kunjungan. "
        "Kunjungan memiliki relasi Asosiasi langsung dengan Pasien (1 objek) dan TenagaMedis (1 objek). Dokter dan Perawat "
        "menerapkan relasi Pewarisan (Inheritance) dari TenagaMedis. TenagaMedis mengimplementasikan interface PelayananMedis, "
        "dan Kunjungan mengimplementasikan interface Comparable&lt;Kunjungan&gt;.",
        style_body
    ))

    # 4. PENERAPAN KONSEP OOP
    story.append(Paragraph("4. Penerapan Konsep Pemrograman Berorientasi Objek (OOP)", style_sec_head))
    story.append(Paragraph(
        "• <strong>Class dan Object:</strong> Diterapkan pada 7 class dan 1 interface. Objek konkret diinstansiasi pada runtime, seperti Dokter (dr. Budi Santoso, Sp.PD), Perawat (Ners Andi Pratama), dan objek Pasien.<br/>"
        "• <strong>Encapsulation:</strong> Seluruh atribut dideklarasikan dengan access modifier private. Akses dan modifikasi nilai dilakukan terkontrol melalui getter, setter, dan constructor. Tidak ada atribut yang dapat diakses langsung dari luar class.<br/>"
        "• <strong>Validation dan Business Rule:</strong> Diterapkan minimal 4 aturan bisnis aktif: (1) Nama pasien tidak boleh kosong; (2) Usia pasien harus &gt; 0 tahun; (3) ID pasien tidak boleh duplikat di Klinik (IllegalArgumentException); dan (4) Kunjungan yang sudah berstatus SELESAI ditolak jika diproses ulang (IllegalStateException).<br/>"
        "• <strong>Relationship Antarobject:</strong> Klinik memiliki relasi Agregasi terhadap daftar Pasien, TenagaMedis, dan Kunjungan. Kunjungan berasosiasi langsung dengan objek Pasien dan TenagaMedis.<br/>"
        "• <strong>Inheritance:</strong> Class Dokter dan Perawat mewarisi atribut umum (idTenagaMedis, nama, spesialisasi) dan method dari superclass TenagaMedis menggunakan kata kunci extends.<br/>"
        "• <strong>Abstract Class:</strong> TenagaMedis dideklarasikan sebagai abstract class yang memiliki abstract method <code>public abstract void beriPelayanan(Pasien pasien, String keluhan)</code>.<br/>"
        "• <strong>Interface:</strong> Interface PelayananMedis diimplementasikan oleh TenagaMedis sebagai kontrak pelayanan. Kunjungan mengimplementasikan interface Comparable&lt;Kunjungan&gt; untuk sorting antrean.<br/>"
        "• <strong>Polymorphism dan Method Overriding:</strong> Method <code>beriPelayanan()</code> dioverride berbeda pada Dokter (diagnosis fisik klinis &amp; resep obat) dan Perawat (triase &amp; tanda vital). Dynamic dispatch dibuktikan pada pemanggilan <code>tenagaMedis.beriPelayanan(pasien, keluhan)</code> di mana pemanggilan melalui referensi superclass menghasilkan tindakan yang berbeda sesuai objek konkret pada runtime.",
        style_body
    ))

    # 5. FITUR KREATIF
    story.append(Paragraph("5. Fitur Kreatif: Sistem Antrean Triase Berdasarkan Tingkat Urgensi", style_sec_head))
    story.append(Paragraph(
        "Fitur kreatif yang dibuat adalah sistem antrean prioritas multi-level berbasis triase kegawatdaruratan medis:<br/>"
        "- <strong>Prioritas 1 (Darurat / Emergency):</strong> Pasien kondisi gawat darurat (contoh: nyeri dada akut, sesak napas).<br/>"
        "- <strong>Prioritas 2 (Mendesak / Urgent):</strong> Pasien kondisi sakit akut stabil (contoh: demam tinggi, batuk rejan).<br/>"
        "- <strong>Prioritas 3 (Reguler / Normal):</strong> Pasien pemeriksaan rutin, kontrol berkala, atau keluhan ringan.<br/>"
        "<strong>Cara Kerja:</strong> Kunjungan mengimplementasikan interface Comparable&lt;Kunjungan&gt;. Logika compareTo mendahulukan angka prioritas terkecil (1 mendahului 2, 2 mendahului 3). Apabila prioritas sama, sistem membandingkan nomor urut pendaftaran (FIFO). Pasien darurat yang mendaftar belakangan secara otomatis melompati antrean reguler untuk dilayani terlebih dahulu.",
        style_body
    ))

    # 6. CLASS DIAGRAM
    story.append(Paragraph("6. Class Diagram", style_sec_head))
    story.append(Paragraph("Diagram kelas teknis standar UML yang sesuai dengan implementasi akhir source code:", style_body))

    png_path = r"d:\TUGAS KULIAH\Semester 3\Pemrograman Berorientasi Objek\UTS-Praktikum-PBO\UTS_TIPE_B\class-diagram.png"
    if os.path.exists(png_path):
        story.append(Image(png_path, width=6.6*inch, height=4.2*inch))
        story.append(Paragraph("Gambar 1. Class Diagram Teknis Sistem Informasi Klinik Sehat Medika", style_caption))

    # 7. PENGUJIAN PROGRAM
    story.append(Paragraph("7. Pengujian Program", style_sec_head))
    story.append(Paragraph("Tabel hasil pengujian faktual yang dijalankan langsung terhadap program:", style_body))

    test_table_data = [
        [Paragraph("No", style_tbl_head), Paragraph("Skenario Pengujian", style_tbl_head), Paragraph("Hasil yang Diharapkan", style_tbl_head), Paragraph("Hasil Aktual & Status", style_tbl_head)],
        [Paragraph("1", style_tbl_cell), Paragraph("Validasi Nama Pasien Kosong ('   ')", style_tbl_cell), Paragraph("Sistem menolak dan melempar exception.", style_tbl_cell), Paragraph("Ditolak: 'Nama pasien tidak boleh kosong!' (Lulus)", style_tbl_cell)],
        [Paragraph("2", style_tbl_cell), Paragraph("Validasi Usia Pasien Negatif (-5)", style_tbl_cell), Paragraph("Sistem menolak dan melempar exception.", style_tbl_cell), Paragraph("Ditolak: 'Usia pasien harus lebih besar dari 0 tahun!' (Lulus)", style_tbl_cell)],
        [Paragraph("3", style_tbl_cell), Paragraph("Pencegahan Duplikasi ID Pasien ('P001')", style_tbl_cell), Paragraph("Sistem menolak pendaftaran ID ganda.", style_tbl_cell), Paragraph("Ditolak: 'Pasien dengan ID [P001] sudah terdaftar...' (Lulus)", style_tbl_cell)],
        [Paragraph("4", style_tbl_cell), Paragraph("Fitur Antrean Prioritas (P1 Darurat vs P3 Reguler)", style_tbl_cell), Paragraph("Pasien darurat melompati antrean reguler.", style_tbl_cell), Paragraph("KJ003 (#3, Prioritas 1) diposisikan di urutan terdepan dan dilayani pertama. (Lulus)", style_tbl_cell)],
        [Paragraph("5", style_tbl_cell), Paragraph("Polimorfisme: Pelayanan Perawat", style_tbl_cell), Paragraph("Mengeksekusi asuhan keperawatan dan triase.", style_tbl_cell), Paragraph("Output: [TINDAKAN ASUHAN KEPERAWATAN] Triase, Tensi, Suhu, Stabilisasi. (Lulus)", style_tbl_cell)],
        [Paragraph("6", style_tbl_cell), Paragraph("Polimorfisme: Pelayanan Dokter", style_tbl_cell), Paragraph("Mengeksekusi diagnosis klinis dan resep.", style_tbl_cell), Paragraph("Output: [TINDAKAN MEDIS DOKTER] Diagnosis klinis, Resep obat, SIP, Biaya. (Lulus)", style_tbl_cell)],
        [Paragraph("7", style_tbl_cell), Paragraph("Validasi Status Kunjungan Selesai", style_tbl_cell), Paragraph("Menolak pemrosesan ulang pada kunjungan selesai.", style_tbl_cell), Paragraph("Ditolak: 'Kunjungan [KJ003] sudah berstatus SELESAI...' (Lulus)", style_tbl_cell)]
    ]
    t_test = Table(test_table_data, colWidths=[0.35*inch, 2.1*inch, 2.1*inch, 2.25*inch])
    t_test.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#F2F2F2')),
        ('BOX', (0,0), (-1,-1), 1, colors.black),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor('#CCCCCC')),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('LEFTPADDING', (0,0), (-1,-1), 5),
        ('RIGHTPADDING', (0,0), (-1,-1), 5),
        ('VALIGN', (0,0), (-1,-1), 'TOP')
    ]))
    story.append(t_test)
    story.append(Spacer(1, 4))

    story.append(Paragraph("Tangkapan layar (log teks) terminal aktual dari eksekusi <code>java Main --demo</code>:", style_body))

    log_snippet = (
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
    t_log = Table([[Paragraph(log_snippet.replace('\n', '<br/>'), style_code)]], colWidths=[6.8*inch])
    t_log.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#F8FAFC')),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor('#CBD5E1')),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
        ('LEFTPADDING', (0,0), (-1,-1), 6),
        ('RIGHTPADDING', (0,0), (-1,-1), 6)
    ]))
    story.append(t_log)
    story.append(Spacer(1, 6))

    # 8. CARA MENJALANKAN PROGRAM
    story.append(Paragraph("8. Cara Menjalankan Program", style_sec_head))
    story.append(Paragraph(
        "Buka terminal pada direktori src project dan jalankan perintah berikut:<br/>"
        "1. <strong>Perintah Kompilasi:</strong><br/>"
        "&nbsp;&nbsp;&nbsp;&nbsp;<code>javac *.java</code><br/>"
        "2. <strong>Perintah Menjalankan Program (Menu Interaktif):</strong><br/>"
        "&nbsp;&nbsp;&nbsp;&nbsp;<code>java Main</code><br/>"
        "3. <strong>Perintah Menjalankan Pengujian Otomatis (Demo Mandiri):</strong><br/>"
        "&nbsp;&nbsp;&nbsp;&nbsp;<code>java Main --demo</code>",
        style_body
    ))

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"[SUKSES] Laporan PDF berhasil diperbarui: {pdf_path}")

if __name__ == "__main__":
    build_practicum_pdf_report()
