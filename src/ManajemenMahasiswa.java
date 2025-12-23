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
        SwingUtilities.invokeLater(() -> ((JButton)sidebar.getComponent(2)).doClick());
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

        p.add(Box.createVerticalStrut(20));


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
        btn.setBorder(new CompoundBorder(new LineBorder(new Color(255,255,255,50)), new EmptyBorder(10, 15, 10, 15)));

        // Efek Hover Mouse
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn != activeMenuButton) btn.setBackground(new Color(52, 152, 219)); }
            public void mouseExited(MouseEvent e) { if (btn != activeMenuButton) btn.setBackground(C_MAIN); }
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

        JLabel lCari = new JLabel("Cari Data: "); lCari.setFont(F_LABEL);
        JTextField tCari = new JTextField(20); tCari.setFont(F_TEXT);
        JButton bCari = createStyledButton("Cari", C_MAIN);
        JButton bAdd = createStyledButton("+ Input Data", new Color(46, 204, 113));

        toolbar.add(lCari); toolbar.add(tCari); toolbar.add(bCari);
        toolbar.add(Box.createHorizontalStrut(20)); toolbar.add(bAdd);

        // Setup Tabel
        String[] cols = {"NIM", "Nama", "Prodi", "Angkatan", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
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
        for(int i=0; i<table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        // Fitur Sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Tombol Aksi Bawah
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(C_BG);
        JButton bEdit = createStyledButton("Update Data", Color.ORANGE);
        JButton bDel = createStyledButton("Hapus Data", new Color(231, 76, 60));
        bottom.add(bEdit); bottom.add(bDel);

        // --- Logic Tombol ---
        bCari.addActionListener(e -> {
            String text = tCari.getText();
            if (text.trim().length() == 0) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        });

        bAdd.addActionListener(e -> showFormDialog(null));

        bEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) JOptionPane.showMessageDialog(this, "Pilih data dulu!");
            else {
                String nim = (String) table.getValueAt(table.convertRowIndexToModel(row), 0);
                Mahasiswa m = listMahasiswa.stream().filter(x -> x.getNim().equals(nim)).findFirst().orElse(null);
                showFormDialog(m);
            }
        });

        bDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) JOptionPane.showMessageDialog(this, "Pilih data dulu!");
            else {
                if (JOptionPane.showConfirmDialog(this, "Yakin hapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == 0) {
                    String nim = (String) table.getValueAt(table.convertRowIndexToModel(row), 0);
                    listMahasiswa.removeIf(m -> m.getNim().equals(nim));
                    refreshTable(); saveData(); addLog("Hapus Data: " + nim);
                }
            }
        });

        p.add(toolbar, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        mainContent.add(p, "LIST");
    }

    // 3. History
    private void createHistoryPage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel l = new JLabel("Riwayat Aktivitas"); l.setFont(F_HEADER);
        l.setBorder(new EmptyBorder(0, 0, 15, 0));

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtLog.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Load data history saat awal
        loadHistory();

        p.add(l, BorderLayout.NORTH);
        p.add(new JScrollPane(txtLog), BorderLayout.CENTER);
        mainContent.add(p, "HIST");
    }

    // ---------------------------------------------------------
    // C. POP-UP DIALOG (INPUT DATA)
    // ---------------------------------------------------------
    private void showFormDialog(Mahasiswa editData) {
        boolean isEdit = (editData != null);
        JDialog d = new JDialog(this, isEdit ? "Edit Data" : "Tambah Data", true);
        d.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(30, 30, 30, 30));

        JTextField tNim = new JTextField();
        JTextField tNama = new JTextField();
        JTextField tProdi = new JTextField();
        JTextField tAngkatan = new JTextField();
        String[] statusOpt = {"Aktif", "Tidak Aktif", "Cuti", "Lulus", "DO"};
        JComboBox<String> cStatus = new JComboBox<>(statusOpt);

        // Styling Component
        Dimension dim = new Dimension(300, 35);
        for(JComponent c : new JComponent[]{tNim, tNama, tProdi, tAngkatan, cStatus}) {
            c.setFont(F_TEXT);
            c.setPreferredSize(dim);
        }
        cStatus.setBackground(Color.WHITE);

        if (isEdit) {
            tNim.setText(editData.getNim()); tNim.setEditable(false);
            tNama.setText(editData.getNama());
            tProdi.setText(editData.getProdi());
            tAngkatan.setText(String.valueOf(editData.getAngkatan()));
            cStatus.setSelectedItem(editData.getStatus());
        }

        // Layout Form
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        addFormRow(form, "NIM:", tNim, y++, gbc);
        addFormRow(form, "Nama Lengkap:", tNama, y++, gbc);
        addFormRow(form, "Program Studi:", tProdi, y++, gbc);
        addFormRow(form, "Angkatan:", tAngkatan, y++, gbc);
        addFormRow(form, "Status:", cStatus, y++, gbc);

        // Tombol Simpan
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bSave = createStyledButton("Simpan", new Color(46, 204, 113));
        JButton bCancel = createStyledButton("Batal", Color.GRAY);

        bSave.addActionListener(e -> {
            try {
                if(tNim.getText().isEmpty() || tNama.getText().isEmpty() || tProdi.getText().isEmpty())
                    throw new Exception("Data wajib diisi semua!");

                int angk = Integer.parseInt(tAngkatan.getText());

                if(isEdit) {
                    editData.setNama(tNama.getText());
                    editData.setProdi(tProdi.getText());
                    editData.setAngkatan(angk);
                    editData.setStatus(cStatus.getSelectedItem().toString());
                    addLog("Update Data: " + editData.getNim());
                } else {
                    String nim = tNim.getText();
                    if(listMahasiswa.stream().anyMatch(m -> m.getNim().equals(nim))) throw new Exception("NIM sudah ada!");

                    listMahasiswa.add(new Mahasiswa(nim, tNama.getText(), tProdi.getText(), angk, cStatus.getSelectedItem().toString()));
                    addLog("Tambah Data: " + nim);
                }
                saveData(); refreshTable(); d.dispose();
                JOptionPane.showMessageDialog(d, "Berhasil disimpan!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage());
            }
        });

        bCancel.addActionListener(e -> d.dispose());
        btnPanel.add(bCancel); btnPanel.add(bSave);

        d.add(form, BorderLayout.CENTER);
        d.add(btnPanel, BorderLayout.SOUTH);
        d.pack(); d.setLocationRelativeTo(this); d.setVisible(true);
    }

    private void addFormRow(JPanel p, String label, JComponent field, int y, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel l = new JLabel(label); l.setFont(F_LABEL); l.setForeground(Color.GRAY);
        p.add(l, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 20, 10, 0);
        p.add(field, gbc);

        gbc.insets = new Insets(10, 0, 10, 0); // Reset padding
    }

    // ---------------------------------------------------------
    // D. LOGIC & DATA
    // ---------------------------------------------------------
    private void refreshTable() {
        if(tableModel == null) return;
        tableModel.setRowCount(0);
        for(Mahasiswa m : listMahasiswa) {
            tableModel.addRow(new Object[]{m.getNim(), m.getNama(), m.getProdi(), m.getAngkatan(), m.getStatus()});
        }
        updateStatistics();
    }

    private void updateStatistics() {
        if(labelsStats[0] == null) return;
        long[] counts = {
                listMahasiswa.size(),
                countStatus("Aktif"), countStatus("Tidak Aktif"), countStatus("Cuti"),
                countStatus("Lulus"), countStatus("DO")
        };
        for(int i=0; i<6; i++) labelsStats[i].setText("" + counts[i]);
    }

    private long countStatus(String s) {
        return listMahasiswa.stream().filter(m -> m.getStatus().equalsIgnoreCase(s)).count();
    }

    private void addLog(String msg) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String entry = "[" + time + "] " + msg + "\n";
        if(txtLog != null) txtLog.append(entry);
        try(BufferedWriter w = new BufferedWriter(new FileWriter(FILE_LOG, true))) { w.write(entry); } catch(Exception e){}
    }

    private void loadHistory() {
        File f = new File(FILE_LOG);
        if(!f.exists()) return;
        try(BufferedReader r = new BufferedReader(new FileReader(f))) {
            String l; while((l = r.readLine()) != null) txtLog.append(l + "\n");
        } catch(Exception e){}
    }

    private void saveData() {
        try(BufferedWriter w = new BufferedWriter(new FileWriter(FILE_CSV))) {
            for(Mahasiswa m : listMahasiswa) { w.write(m.toString()); w.newLine(); }
        } catch(Exception e){}
    }

    private void loadData() {
        File f = new File(FILE_CSV);
        if(!f.exists()) return;
        try(BufferedReader r = new BufferedReader(new FileReader(f))) {
            String l;
            while((l = r.readLine()) != null) {
                String[] p = l.split(",");
                if(p.length >= 5) listMahasiswa.add(new Mahasiswa(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4]));
            }
            refreshTable();
        } catch(Exception e){}
    }

    // --- Helper UI ---
    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(F_LABEL); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(8, 15, 8, 15)); b.setCursor(new Cursor(12));
        return b;
    }

    private void addStatCard(JPanel parent, String title, String sym, Color c, JLabel valLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 6, 0, 0, c));

        JLabel s = new JLabel(sym, 0); s.setFont(new Font("Times New Roman", 1, 48));
        s.setForeground(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
        s.setPreferredSize(new Dimension(80, 0));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false); info.setBorder(new EmptyBorder(15, 10, 15, 10));

        JLabel t = new JLabel(title, 0); t.setFont(F_LABEL); t.setForeground(Color.GRAY);
        valLabel.setFont(new Font("Times New Roman", 1, 36)); valLabel.setForeground(C_MAIN); valLabel.setHorizontalAlignment(0);

        info.add(t); info.add(valLabel);
        card.add(s, BorderLayout.WEST); card.add(info, BorderLayout.CENTER);
        card.setBorder(new CompoundBorder(new LineBorder(new Color(220,220,220)), card.getBorder()));
        parent.add(card);
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        SwingUtilities.invokeLater(() -> new ManajemenMahasiswa().setVisible(true));
    }
}