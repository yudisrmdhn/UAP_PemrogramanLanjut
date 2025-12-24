# Data Mahasiswa UMM

**Data Mahasiswa** adalah aplikasi manajemen data akademik berbasis desktop yang dikembangkan sebagai tugas akhir mata kuliah **Pemrograman Lanjut 2025**.

Aplikasi ini dibangun menggunakan **Java Swing** dengan antarmuka yang bersih (*clean design*), menggunakan font standar akademik (Times New Roman), serta dilengkapi dengan fitur CRUD dan pencatatan riwayat aktivitas secara otomatis.

---

## Fitur Utama

Aplikasi ini memiliki fitur unggulan sebagai berikut:

### 1. Dashboard Akademik
* Tampilan ringkasan yang bersih dan minimalis.
* 6 Kartu statistik real-time untuk memantau jumlah mahasiswa berdasarkan status (Total, Aktif, Tidak Aktif, Cuti, Lulus, DO).

### 2. Manajemen Data (CRUD)
* **Input & Edit:** Menggunakan *Pop-up Dialog* (JDialog) untuk menambah atau mengubah data.
* **Tabel Data:** Menampilkan daftar mahasiswa dengan desain rapi (Rata Tengah) agar mudah dibaca.
* **Hapus Data:** Fitur penghapusan dengan konfirmasi keamanan (*Confirmation Dialog*).

### 3. Pencarian & Pengurutan
* **Search:** Pencarian data mahasiswa secara *real-time* berdasarkan teks (NIM/Nama/Prodi).
* **Sorting:** Pengurutan otomatis pada header tabel (Ascending/Descending).

### 4. Sistem Penyimpanan (File Handling)
* **Database CSV:** Data mahasiswa disimpan permanen di file `DataMahasiswa.csv`.
* **Auto-Save/Load:** Aplikasi otomatis membaca data saat dibuka dan menyimpan perubahan secara instan.

### 5. Jejak Aktivitas (History Log)
* Setiap aktivitas (Tambah, Edit, Hapus) dicatat ke dalam file `history.txt` lengkap dengan waktu kejadian (Timestamp).
* Riwayat dapat dilihat langsung melalui menu "Laporan" di aplikasi.

---

## Penjelasan Teknis Kode

Aplikasi ini menggunakan struktur **Single-Class Application** yang terorganisir rapi.

### A. Model Data (`class Mahasiswa`)
Merepresentasikan objek mahasiswa dengan konsep **Encapsulation** (atribut `private` dengan `getter/setter`). Method `toString()` di-override untuk format penyimpanan CSV.

### B. Tampilan & Logika (`class ManajemenMahasiswa`)
Kelas utama turunan `JFrame` yang menangani:
1.  **Manajemen Layout:** Menggunakan kombinasi `BorderLayout`, `BoxLayout`, dan `CardLayout` untuk navigasi yang responsif tanpa membuka banyak jendela.
2.  **Kustomisasi Tampilan:** Menggunakan font `Times New Roman` dan `DefaultTableCellRenderer` untuk perataan teks tabel.
3.  **Logika CRUD:** Satu dialog dinamis untuk Tambah/Edit dan validasi input untuk mencegah duplikasi NIM.
4.  **File Handling (I/O):** Menggunakan `BufferedReader` dan `BufferedWriter` untuk operasi baca/tulis file `DataMahasiswa.csv` dan `history.txt`.

---

## Cara Menjalankan Program

Anda dapat menjalankan aplikasi ini menggunakan salah satu dari tiga metode berikut:

### Metode 1: Menggunakan IDE (Disarankan untuk Coding)
Cara termudah jika Anda ingin melihat atau mengedit kode.
1.  Buka **IntelliJ IDEA**, **NetBeans**, atau **Eclipse**.
2.  Pilih menu `Open Project` dan arahkan ke folder proyek ini.
3.  Buka file `src/org/example/ManajemenMahasiswa.java`.
4.  Klik tombol **Run** (▶) atau tekan `Shift + F10`.

### Metode 2: Menggunakan Terminal / CMD (Manual)
Cocok untuk menjalankan program tanpa membuka aplikasi berat.
1.  Buka Command Prompt (CMD) atau Terminal.
2.  Arahkan ke direktori `src` proyek:
    ```bash
    cd path/ke/folder/project/src
    ```
3.  Compile kode program:
    ```bash
    javac org/example/ManajemenMahasiswa.java
    ```
4.  Jalankan program:
    ```bash
    java org.example.ManajemenMahasiswa
    ```

### Metode 3: Menggunakan File JAR (Executable)
Jika Anda sudah melakukan *build artifact* menjadi file JAR.
1.  Pastikan Anda berada di folder tempat file `.jar` berada.
2.  Jalankan perintah berikut di terminal:
    ```bash
    java -jar PortalMahasiswa.jar
    ```
3.  Atau cukup **Double Click** file `.jar` tersebut (jika OS mendukung).

> **Catatan:** File `DataMahasiswa.csv` dan `history.txt` akan dibuat otomatis oleh aplikasi di folder yang sama dengan tempat aplikasi dijalankan jika file tersebut belum tersedia.

---
## Anggota Kelompok

**Tugas UAP Pemrograman Lanjut 2025**

| Nama Mahasiswa            | NIM             |
|:--------------------------|:----------------|
| Gagah Yudhistira Ramadhan | 202410370110323 |
| Bahru Ni'am                | 202410370110277 |