# MASTER PLAN: SuperApp Alat Bantu UMKM (Kalkulator HPP & Finansial Bisnis)

Dokumen ini adalah **panduan utama (Single Source of Truth)** untuk roadmap pengembangan fitur, arsitektur sistem, dan standar teknis aplikasi **Kalkulator HPP**.

---

## 🎯 1. Visi Produk
Menjadikan aplikasi ini bukan hanya sekadar kalkulator resep biasa, melainkan **SuperApp Finansial & Alat Bantu Strategi Bisnis #1 bagi pelaku UMKM** (Kuliner/F&B, Toko Retail, dan Jasa).
- **Offline-First**: 100% data tersimpan di perangkat lokal (Room SQLite), tanpa kewajiban login dan tanpa butuh koneksi internet.
- **Praktis & Akurat**: Menjawab kebutuhan riil pengusaha (menghindari rugi jualan di ojek online/marketplace, tahu target balik modal, dan resep terstandarisasi).

---

## 🛠️ 2. Fondasi Teknis & Arsitektur
- **Platform**: Android Native (Kotlin)
- **UI Toolkit**: Jetpack Compose + Material 3 (Declarative UI)
- **Arsitektur**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Penyimpanan Lokal**: Room SQLite (`room-runtime:2.6.1`, `room-ktx`, `ksp`)
- **State Management**: Kotlin Coroutines & `StateFlow`
- **Aturan Pengembangan**:
  - Dilarang menjalankan perintah build otomatis (misal: `./gradlew build` atau sejenisnya) sesuai preferensi pengguna.

---

## 📌 3. Status Fitur Saat Ini (v1.0 - Selesai)
- [x] **Kalkulasi HPP Multikategori**: Formulir dinamis untuk F&B (Bahan baku + kemasan + tenaga kerja + operasional), Retail, dan Jasa.
- [x] **Kalkulasi Margin & Rekomendasi Harga Jual**: Perhitungan otomatis HPP per unit, nominal keuntungan, dan harga jual ideal.
- [x] **Simulasi Slider Margin Interaktif**: Slider real-time di layar hasil untuk melihat dampak perubahan persentase keuntungan.
- [x] **Database Offline (Room SQLite)**: Penyimpanan permanen data produk dan relasi bahan (*Cascade Delete*).
- [x] **Daftar Riwayat & Pencarian**: Filter tab kategori (Semua, Retail, F&B, Jasa) dan pencarian nama produk.
- [x] **Ekspor & Share Slip Gambar**: Tangkap visual hasil perhitungan dan bagikan via WhatsApp / media sosial.
- [x] **Onboarding Screen**: Pengenalan fungsi aplikasi untuk pengguna baru.

---

## 🗺️ 4. Roadmap Fitur (SuperApp Milestones)

### 📍 MILESTONE 1: Fondasi Resep & Fleksibilitas (Prioritas Utama)
*Fokus: Memudahkan pengusaha mengelola dan mengubah data tanpa repot input berulang kali.*

1. **Fitur Edit Kalkulasi**:
   - Membuka kembali formulir HPP dengan data yang sudah terisi untuk diedit jika ada perubahan takaran/harga.
2. **Duplikasi Produk (Varian Resep / Clone)**:
   - Tombol satu sentuhan untuk menduplikasi produk yang ada (misal: varian rasa *Kopi Susu Aren* di-copy menjadi *Kopi Susu Coklat*, atau varian *Reguler* ke *Large*).
3. **Master Database Bahan Baku (Recipe Vault)**:
   - Database bahan baku tersimpan (nama bahan, harga beli, satuan, berat kemasan).
   - Saat membuat menu baru, pengguna tinggal memilih dari daftar bahan yang sudah ada.
   - **Fitur Dampak Kenaikan Harga**: Jika harga satu bahan baku naik, sistem bisa menampilkan produk mana saja yang HPP-nya terpengaruh.

---

### 📍 MILESTONE 2: Saluran Distribusi & Komisi Penjualan
*Fokus: Melindungi margin keuntungan dari potongan komisi aplikasi pihak ketiga.*

1. [x] **Kalkulator Harga Merchant Online (Ojol & Marketplace)**:
   - Template komisi otomatis:
     - **GoFood / GrabFood / ShopeeFood**: Komisi 20% + biaya layanan.
     - **TikTok Shop / Shopee / Tokopedia**: Potongan admin marketplace (6.5% - 8.5%).
   - Rumus markup harga otomatis: Menghitung berapa harga yang harus dicantumkan di aplikasi agar keuntungan bersih UMKM tetap utuh.
2. [x] **Multi-tier Pricing (Harga Bertingkat)**:
   - Menghitung harga jual berbeda untuk: *Harga Dine-in / Offline*, *Harga Reseller / Grosir*, dan *Harga Aplikasi Online*.

---

### 📍 MILESTONE 3: Analisis Kelayakan Bisnis & Target Penjualan
*Fokus: Memberikan arahan strategi bisnis agar UMKM tidak nombok dan tahu target harian.*

1. [x] **Kalkulator BEP (Break-Even Point / Titik Impas Usaha)**:
   - Input Biaya Tetap Bulanan (Sewa tempat, gaji karyawan, tagihan listrik & air, internet).
   - Menghitung target minimal produk yang wajib terjual per hari dan per bulan agar usaha tidak merugi.
2. [x] **Goal Planner (Target Profit Bulanan)**:
   - Input target keuntungan bersih pribadi (misal: Rp 10.000.000/bulan).
   - Aplikasi memecah target menjadi kuota penjualan harian berdasarkan kombinasi margin produk.
3. [x] **Kalkulator Modal Awal Usaha (CapEx Planner)**:
   - Perencanaan modal buka gerai baru (biaya perlengkapan/alat, renovasi booth, dan cadangan dana darurat operasional 3 bulan pertama).

---

### 📍 MILESTONE 4: Ekspor Dokumen & Standarisasi SOP
*Fokus: Membantu operasional dapur dan arsip pembukuan.*

1. [x] **Cetak SOP Resep Dapur (Mode Karyawan)**:
   - Mencetak kartu resep takaran baku untuk karyawan dapur **tanpa mencantumkan harga modal / persentase margin keuntungan** (menjaga rahasia dapur pemilik).
2. [x] **Cetak Slip Laporan Lengkap (Mode Owner)**:
   - Laporan HPP lengkap dengan rincian biaya, margin, multi-tier ojol, dan rekomendasi harga.
3. [x] **Ekspor Data ke CSV / Excel**:
   - Memindahkan seluruh riwayat kalkulasi HPP dan riwayat Buku Kas ke spreadsheet berformat UTF-8 BOM untuk diolah di Google Sheets / Microsoft Excel.

---

### 📍 MILESTONE 5: Buku Kas Digital & Arus Kas UMKM (Cashbook & Keuangan Harian)
*Fokus: Mengukur performa keuntungan riil harian serta mencatat uang masuk dan keluar secara rapi.*

1. **Buku Kas Pengeluaran & Pemasukan (Cash In / Cash Out)**:
   - **Catat Pemasukan (Uang Masuk)**: Omset penjualan harian, modal awal/tambahan, atau pemasukan lain.
   - **Catat Pengeluaran (Uang Keluar)**: Belanja stok bahan, operasional (listrik, air, gas, sewa tempat, kuota/wifi), gaji karyawan, dan pengeluaran darurat.
   - **Kategori Pos Anggaran**: Tagging otomatis (Bahan Baku, Operasional, Gaji, dll) agar pemilik tahu pos mana yang memakan biaya terbesar.
2. **Integrasi Penjualan Cepat dengan HPP (Quick Sales / POS)**:
   - Catat menu/produk yang laku terjual langsung memilih dari database produk HPP.
   - Otomatis menghitung: *Omset Penjualan - Modal HPP Terpakai = Laba Kotor Real-time*.
3. **Laporan Arus Kas Ringkas (Net Cashflow Summary)**:
   - Ringkasan saldo kas usaha berjalan (*Saldo Kas = Total Masuk - Total Keluar*).
   - Indikator kesehatan finansial harian & bulanan (status surplus atau defisit).
   - Ekspor rekap buku kas bulanan ke PDF / Excel untuk arsip pembukuan.

---

## 📐 5. Standar Kode & Struktur Folder
```
com.seal.hppcalculator/
├── data/
│   ├── local/          # Room Entity, DAO, Database
│   ├── model/          # Domain Models & Business Calculation Logic
│   └── repository/     # Data Repository
├── ui/
│   ├── screen/         # Main screens (Home, Create, Result, Splash, Onboarding)
│   ├── components/     # Reusable atomic UI components (Buttons, Fields, Cards)
│   └── theme/          # Material 3 Color, Type, Shape & Theme tokens
└── viewmodel/          # StateFlow & ViewModel Architecture
```

---

## 📝 Catatan Perkembangan (Changelog & Milestone Progress)
- **v1.0**: Rilis fondasi perhitungan HPP, 3 kategori usaha, slider margin, simpan SQLite lokal, dan ekspor gambar.
- **v1.1**: Redesain tampilan modern minimalis (Soft Border), Onboarding profil pengguna, ilustrasi Empty State, dan **Buku Kas Digital UMKM (Milestone 5)**.
- **v1.2 (Selesai)**:
  - **Milestone 1**: Fitur Edit Resep HPP (in-place update Room SQLite) & Duplikasi Produk / Varian Resep 1-klik.
  - **Inspirasi Resep JSON (`sample_recipes.json`)**: Preset resep riil UMKM (Tahu Crispy Gurih, Es Kopi Susu Aren, Dimsum Ayam, Kaos Distro, Cuci Sepatu).
  - **Simulasi Target Penjualan & Proyeksi Laba Bersih**: Kalkulator interaktif hitung untung harian/bulanan berdasarkan target porsi jualan dan durasi hari.
- **v1.3 (Selesai - Milestone 2: Saluran Distribusi & Komisi Penjualan)**:
  - **Kalkulator Harga Merchant Online (Ojol & Marketplace)**: Formula markup anti-tekor otomatis melindungi margin bersih UMKM setelah dipotong komisi (GoFood/GrabFood 20%, ShopeeFood 20%, TikTok Shop 8.5%, Shopee Star 6.5%, Tokopedia 6.5%).
  - **Multi-tier Pricing Bento Card**: Perbandingan harga Dine-in / Offline, Ojek Online, Marketplace, dan Grosir / Reseller langsung di `ResultScreen`.
  - **Kustomisasi Komisi & Salin ke WhatsApp**: Bottom sheet kustomisasi komisi dengan tombol salin daftar harga resmi ke clipboard.
  - **Kalkulator Cepat Markup Ojol di Dashboard**: Tool instan di Home Screen untuk hitung harga jual online kapan saja tanpa perlu buat resep terlebih dahulu.
- **v1.4 (Selesai - Milestone 3: Analisis Kelayakan Bisnis & Target Penjualan)**:
  - **Kalkulator BEP (Titik Impas Usaha)**: Hitung kuota penjualan harian dan bulanan yang wajib tercapai agar biaya sewa, gaji, dan utilitas tertutup.
  - **Goal Planner (Target Laba Bersih Bulanan)**: Input target ingin mengantongi laba bersih (misal Rp 5 Jt / 10 Jt per bulan), sistem memecah kuota porsi jualan per hari secara otomatis.
  - **CapEx Planner & Payback Period**: Perencana modal awal buka cabang/usaha baru + simulasi berapa bulan modal akan kembali lunas.
- **v1.5 (Selesai - Milestone 4: Ekspor Dokumen & Standarisasi SOP Dapur)**:
  - **Standarisasi SOP Dapur Karyawan**: Kartu takaran baku untuk koki/karyawan dapur dengan **rahasia modal dan margin keuntungan disembunyikan 100%**.
  - **Laporan Lengkap Manajerial (Owner Mode)**: Rangkuman finansial menyeluruh siap cetak atau share ke WhatsApp.
  - **Ekspor Riwayat HPP ke Excel (.CSV)**: Tombol ekspor 1-klik di Tab Data HPP menghasilkan spreadsheet ber-BOM UTF-8 kompatibel penuh dengan Microsoft Excel & Google Sheets.
  - **Ekspor Buku Kas ke Excel (.CSV)**: Rekap arus kas masuk/keluar bulanan siap arsip atau pembukuan akuntansi.
- **🎉 SUPERAPP UMKM FINANSE STATUS: ALL 5 MILESTONES COMPLETED!**
