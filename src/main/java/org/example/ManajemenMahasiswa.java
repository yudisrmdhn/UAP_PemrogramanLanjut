package org.example;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// ==========================================
// 1. MODEL DATA MAHASISWA
// ==========================================
class Mahasiswa {
    private String nim, nama, prodi, status;
    private int angkatan;

    public Mahasiswa(String nim, String nama, String prodi, int angkatan, String status) {
        this.nim = nim;
        this.nama = nama;
        this.prodi = prodi;
        this.angkatan = angkatan;
        this.status = status;
    }

    // Format penyimpanan ke CSV
    @Override
    public String toString() {
        return nim + "," + nama + "," + prodi + "," + angkatan + "," + status;
    }

    // Getters untuk mengambil data
    public String getNim() { return nim; }
    public String getNama() { return nama; }
    public String getProdi() { return prodi; }
    public int getAngkatan() { return angkatan; }
    public String getStatus() { return status; }

    // Setters untuk update data
    public void setNama(String nama) { this.nama = nama; }
    public void setProdi(String prodi) { this.prodi = prodi; }
    public void setAngkatan(int angkatan) { this.angkatan = angkatan; }
    public void setStatus(String status) { this.status = status; }
}

// ==========================================
// 2. KELAS UTAMA APLIKASI
// ==========================================
public class ManajemenMahasiswa extends JFrame {

    // --- Konfigurasi Warna & Font ---
    final Color C_MAIN = new Color(44, 62, 80);    // Warna Biru Tua (Sidebar)
    final Color C_BG = new Color(240, 242, 245);   // Warna Abu Muda (Background)

    // Font Times New Roman (Sesuai Request)
    final Font F_HEADER = new Font("Times New Roman", Font.BOLD, 26);
    final Font F_LABEL = new Font("Times New Roman", Font.BOLD, 15);
    final Font F_TEXT = new Font("Times New Roman", Font.PLAIN, 15);

    // Data Storage
    private ArrayList<Mahasiswa> listMahasiswa = new ArrayList<>();
    private final String FILE_CSV = "DataMahasiswa.csv";
    private final String FILE_LOG = "history.txt";

    // Komponen GUI Global
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContent = new JPanel(cardLayout);
    private JButton activeMenuButton = null; // Menyimpan tombol menu yg sedang aktif

    // Komponen Dashboard & Tabel
    private JLabel[] labelsStats = new JLabel[6]; // Label angka statistik
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextArea txtLog;

    public ManajemenMahasiswa() {
        setTitle("Aplikasi Manajemen Data Mahasiswa");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();       // 1. Buat Tampilan
        loadData();     // 2. Muat Data
    }

    // ---------------------------------------------------------
    // A. SETUP TAMPILAN (UI)
    // ---------------------------------------------------------
    private void initUI() {
        setLayout(new BorderLayout());

        // Sidebar (Kiri)
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main Content (Kanan)
        mainContent.setBackground(C_BG);

        // Menambahkan Halaman-halaman
        createDashboardPage();
        createListPage();
        createHistoryPage();

        add(mainContent, BorderLayout.CENTER);

        // Default buka Dashboard
        SwingUtilities.invokeLater(() -> ((JButton) sidebar.getComponent(2)).doClick());
    }

    private JPanel createSidebar() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_MAIN);
        p.setPreferredSize(new Dimension(230, getHeight()));
        p.setBorder(new EmptyBorder(30, 15, 30, 15));

        // Judul Aplikasi
        JLabel title = new JLabel("<html><center>DATA<br>MAHASISWA</center></html>", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);
        p.add(title);
        p.add(Box.createVerticalStrut(40));

        // Menu Navigasi
        addMenuButton(p, "Dashboard", "DASH");
        addMenuButton(p, "Data Table", "LIST");
        addMenuButton(p, "Laporan", "HIST");

        p.add(Box.createVerticalGlue()); // Spacer ke bawah

        // Tombol Tambah Cepat
        JButton btnAdd = createStyledButton("+ Input Data", new Color(46, 204, 113));
        btnAdd.setMaximumSize(new Dimension(200, 45));
        btnAdd.setAlignmentX(CENTER_ALIGNMENT);
        btnAdd.addActionListener(e -> showFormDialog(null));
        p.add(btnAdd);

        p.add(Box.createVerticalStrut(20));
        JLabel footer = new JLabel("UAP 2025");
        footer.setForeground(Color.WHITE);
        footer.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        footer.setAlignmentX(CENTER_ALIGNMENT);
        p.add(footer);

        return p;
    }

    private void addMenuButton(JPanel panel, String text, String cardName) {
        JButton btn = createStyledButton(text, C_MAIN);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        // Border putih tipis agar terlihat rapi
        btn.setBorder(new CompoundBorder(new LineBorder(new Color(255, 255, 255, 50)), new EmptyBorder(10, 15, 10, 15)));

        // Efek Hover Mouse
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeMenuButton) btn.setBackground(new Color(52, 152, 219));
            }

            public void mouseExited(MouseEvent e) {
                if (btn != activeMenuButton) btn.setBackground(C_MAIN);
            }
        });

        // Aksi Klik
        btn.addActionListener(e -> {
            // Reset tombol lama
            if (activeMenuButton != null) activeMenuButton.setBackground(C_MAIN);
            // Set tombol baru
            activeMenuButton = btn;
            activeMenuButton.setBackground(new Color(52, 152, 219)); // Biru Terang

            // Ganti Halaman
            cardLayout.show(mainContent, cardName);
            if (cardName.equals("DASH")) updateStatistics(); // Update angka jika masuk dashboard
        });

        panel.add(btn);
        panel.add(Box.createVerticalStrut(10)); // Jarak antar tombol
    }

    // ---------------------------------------------------------
    // B. HALAMAN-HALAMAN
    // ---------------------------------------------------------

    // 1. Dashboard
    private void createDashboardPage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Ringkasan Akademik");
        title.setFont(F_HEADER);
        title.setForeground(C_MAIN);
        title.setBorder(new EmptyBorder(0, 0, 30, 0));
        p.add(title, BorderLayout.NORTH);

        // Grid 2 Baris x 3 Kolom
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setOpaque(false);

        // Data Statistik
        String[] titles = {"Total Mahasiswa", "Mahasiswa Aktif", "Tidak Aktif", "Cuti", "Lulus", "Drop Out"};
        String[] symbols = {"M", "A", "T", "C", "L", "D"};
        Color[] colors = {
                new Color(241, 196, 15), // Kuning
                new Color(46, 204, 113), // Hijau
                Color.GRAY,              // Abu
                new Color(52, 152, 219), // Biru
                new Color(155, 89, 182), // Ungu
                new Color(231, 76, 60)   // Merah
        };

        for (int i = 0; i < 6; i++) {
            labelsStats[i] = new JLabel("0"); // Inisialisasi label angka
            addStatCard(grid, titles[i], symbols[i], colors[i], labelsStats[i]);
        }

        p.add(grid, BorderLayout.CENTER);
        mainContent.add(p, "DASH");
    }

    // 2. Data Table
    private void createListPage() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Toolbar Atas
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(C_BG);

        JLabel lCari = new JLabel("Cari Data: ");
        lCari.setFont(F_LABEL);
        JTextField tCari = new JTextField(20);
        tCari.setFont(F_TEXT);
        JButton bCari = createStyledButton("Cari", C_MAIN);
        JButton bAdd = createStyledButton("+ Tambah", new Color(46, 204, 113));

        toolbar.add(lCari);
        toolbar.add(tCari);
        toolbar.add(bCari);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(bAdd);

        // Setup Tabel
        String[] cols = {"NIM", "Nama", "Prodi", "Angkatan", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(F_TEXT);
        table.getTableHeader().setFont(F_LABEL);
        table.getTableHeader().setBackground(C_MAIN);
        table.getTableHeader().setForeground(Color.WHITE);

        // Rata Tengah Isi Tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        // Fitur Sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

    }
}