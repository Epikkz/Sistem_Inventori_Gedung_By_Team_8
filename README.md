STUDI KASUS 8 : SISTEM INVETORI GEDUNG
Deskripsi: Sebuah perusahaan logistik ingin mengelola data barang di gudang mereka. Sistem ini harus mampu mencatat stok barang, menambah dan mengurangi jumlah barang, serta menampilkan laporan stok terkini.

## Overview Kode
<details>
  <summary> Klik untuk menampilkan Main.Java</summary>
<pre lang="markdown">
  package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class Main extends Application {

    private Gudang gudang = new Gudang();
    private ObservableList<BarangGudang> dataBarang = FXCollections.observableArrayList();
    private TableView<BarangGudang> tabel = new TableView<>();
    private TextField searchField = new TextField();

    @Override
    public void start(Stage primaryStage) {
        // Data awal
        gudang.tambahBarang(new BarangGudang("A001", "Monitor", 10));
        gudang.tambahBarang(new BarangGudang("A002", "Keyboard", 15));
        gudang.tambahBarang(new BarangGudang("A003", "Mouse", 20));
        dataBarang.addAll(gudang.getDaftarBarang());
        
        //icons
        primaryStage.getIcons().add(
        	    new javafx.scene.image.Image(getClass().getResourceAsStream("icon.png"))
        	);

        Image PlusImg = new Image(getClass().getResourceAsStream("plus.png"));
        Image minusImg = new Image(getClass().getResourceAsStream("minus.png"));
        
        // --- Kolom Kode ---
        TableColumn<BarangGudang, String> kolomKode = new TableColumn<>("Kode");
        kolomKode.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getKodeBarang())
        );

        // --- Kolom Nama ---
        TableColumn<BarangGudang, String> kolomNama = new TableColumn<>("Nama Barang");
        kolomNama.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getNamaBarang())
        );

        // --- Kolom Stok ---
        TableColumn<BarangGudang, Integer> kolomStok = new TableColumn<>("Stok");
        kolomStok.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getJumlahStok()).asObject()
        );

        // ✨ Edit langsung stok dari tabel
        kolomStok.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        kolomStok.setOnEditCommit((TableColumn.CellEditEvent<BarangGudang, Integer> e) -> {
            BarangGudang barang = e.getRowValue();
            Integer stokBaru = e.getNewValue();
            barang.setJumlahStok(stokBaru); // langsung set stok baru
            dataBarang.setAll(gudang.getDaftarBarang());
        });

        tabel.getColumns().addAll(kolomKode, kolomNama, kolomStok);
        tabel.setItems(dataBarang);
        tabel.setEditable(true);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Input Form ---
        TextField kodeField = new TextField();
        kodeField.setPromptText("Kode Barang");

        TextField namaField = new TextField();
        namaField.setPromptText("Nama Barang");

        TextField stokField = new TextField();
        stokField.setPromptText("Stok");

        Button tambahBtn = new Button("Tambah Barang");
        Button hapusBtn = new Button("Hapus Barang");
        Button tambahStokBtn = new Button("Tambah Stok");
        Button kurangiStokBtn = new Button("Kurangi Stok");
        
        tambahStokBtn.setGraphic(new ImageView(PlusImg));
        kurangiStokBtn.setGraphic(new ImageView(minusImg));

        // === Aksi tombol stok ===
        tambahStokBtn.setOnAction(e -> {
            BarangGudang barang = tabel.getSelectionModel().getSelectedItem();
            if (barang != null) {
                barang.tambahStok(1);
                dataBarang.setAll(gudang.getDaftarBarang());
            } else {
                alert("Pilih Barang", "Pilih barang yang ingin ditambah stoknya!");
            }
        });

        kurangiStokBtn.setOnAction(e -> {
            BarangGudang barang = tabel.getSelectionModel().getSelectedItem();
            if (barang != null) {
                barang.kurangiStok(1);
                dataBarang.setAll(gudang.getDaftarBarang());
            } else {
                alert("Pilih Barang", "Pilih barang yang ingin dikurangi stoknya!");
            }
        });
        
        // Tombol Tambah Barang
        tambahBtn.setOnAction(e -> {
            try {
                String kode = kodeField.getText();
                String nama = namaField.getText();
                int stok = Integer.parseInt(stokField.getText());

                if (kode.isEmpty() || nama.isEmpty()) {
                    alert("Input Error", "Kode dan nama barang harus diisi!");
                    return;
                }

                BarangGudang b = new BarangGudang(kode, nama, stok);
                gudang.tambahBarang(b);
                dataBarang.setAll(gudang.getDaftarBarang());

                kodeField.clear();
                namaField.clear();
                stokField.clear();
            } catch (NumberFormatException ex) {
                alert("Input Error", "Stok harus berupa angka!");
            }
        });

        // Tombol Hapus
        hapusBtn.setOnAction(e -> {
            BarangGudang barang = tabel.getSelectionModel().getSelectedItem();
            if (barang != null) {
                gudang.getDaftarBarang().remove(barang);
                dataBarang.setAll(gudang.getDaftarBarang());
            } else {
                alert("Hapus Gagal", "Pilih barang yang ingin dihapus terlebih dahulu!");
            }
        });
        
        // 🔎 Fitur Pencarian
        searchField.setPromptText("Cari berdasarkan kode...");
        searchField.textProperty().addListener((obs, oldText, newText) -> filterBarang(newText));

        // --- Layout ---
        HBox formInput = new HBox(10, kodeField, namaField, stokField, tambahBtn, hapusBtn);
        formInput.setPadding(new Insets(10));

        // ⬇️ Tambahkan tombol tambah/kurangi stok di bawah tabel
        HBox stokButtons = new HBox(10, tambahStokBtn, kurangiStokBtn);
        stokButtons.setPadding(new Insets(10));

        VBox root = new VBox(10, searchField, tabel, stokButtons, formInput);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 750, 420);
        primaryStage.setTitle("Sistem Inventori Gedung - Made By Epik (Vikri)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- Fitur Filter Barang ---
    private void filterBarang(String kode) {
        if (kode == null || kode.isEmpty()) {
            dataBarang.setAll(gudang.getDaftarBarang());
        } else {
            ObservableList<BarangGudang> filtered = FXCollections.observableArrayList();
            for (BarangGudang b : gudang.getDaftarBarang()) {
                if (b.getKodeBarang().toLowerCase().contains(kode.toLowerCase())) {
                    filtered.add(b);
                }
            }
            dataBarang.setAll(filtered);
        }
    }

    // --- Alert Helper ---
    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new javafx.scene.image.Image(
            getClass().getResourceAsStream("error.png")
        ));
        
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
</pre>
</details>

Kode di atas adalah program inventori gudang sederhana yang dibuat dengan JavaFX. Aplikasi ini menampilkan daftar barang dalam bentuk tabel, lengkap dengan kode, nama, dan jumlah stok.

Pengguna bisa menambah barang baru, menghapus barang, mencari barang berdasarkan kode, serta menambah atau mengurangi stok dengan menekan tombol atau mengedit langsung di tabel. Semua data ditampilkan secara interaktif dan otomatis diperbarui di layar.

Selain itu, aplikasi ini juga memiliki ikon untuk mempercantik tampilan dan alert (pesan pemberitahuan) agar pengguna tahu jika terjadi kesalahan, misalnya saat input tidak valid atau belum memilih barang.

Secara singkat, kode ini membuat aplikasi gudang berbasis GUI yang mudah digunakan untuk mengelola data barang secara visual, cepat, dan efisien.

<br>

<details>
  <summary> Klik untuk menampilkan Gudang.Java</summary>
<pre lang="markdown">
package application;

import java.util.ArrayList;
import java.util.List;

public class Gudang {
  
    private List<BarangGudang> daftarBarang;

    public Gudang() {
        daftarBarang = new ArrayList<>();
    }

    // Menambah barang ke daftar gudang
    public void tambahBarang(BarangGudang b) {
        daftarBarang.add(b);
        System.out.println("Barang berhasil ditambahkan: " + b.getNamaBarang());
    }

    // Mencari barang berdasarkan kode
    public BarangGudang cariBarang(String kode) {
        for (BarangGudang b : daftarBarang) {
            if (b.getKodeBarang().equalsIgnoreCase(kode)) {
                return b; 
            }
        }
        return null; 
    }

    // Menampilkan seluruh daftar barang
    public void tampilkanSemuaBarang() {
        if (daftarBarang.isEmpty()) {
            System.out.println("Gudang kosong!");
        } else {
            System.out.println("Daftar Barang di Gudang:");
            for (BarangGudang b : daftarBarang) {
                System.out.println(b);
            }
        }
    }

    // PAKAI GETTER BOSS
    public List<BarangGudang> getDaftarBarang() {
        return daftarBarang;
    }
}
</pre>
</details>

Kode di atas adalah kelas Gudang yang berfungsi sebagai tempat penyimpanan dan pengelola data barang. Di dalamnya terdapat daftar barang yang disimpan dalam bentuk list. Kelas ini menyediakan beberapa metode untuk menambah barang baru, mencari barang berdasarkan kode, serta menampilkan seluruh isi gudang.

Saat barang baru ditambahkan, program akan menampilkan pesan konfirmasi di konsol. Jika pengguna mencari barang berdasarkan kode, metode akan mengembalikan objek barang yang sesuai, atau null jika tidak ditemukan. Selain itu, ada juga metode untuk menampilkan semua barang yang tersimpan, dan jika daftar masih kosong, akan muncul pesan bahwa gudang belum berisi barang apa pun.

Secara singkat, kelas ini bertugas mengatur logika dasar penyimpanan dan pengelolaan data barang agar dapat digunakan oleh bagian tampilan utama aplikasi.

<br>

<details>
  <summary> Klik untuk menampilkan BarangGudang.Java</summary>
<pre lang="markdown">
package application;

public class BarangGudang {
	private String kodeBarang;
	private String namaBarang;
	private int jumlahStok;

	public BarangGudang(String kodeBarang, String namaBarang, int jumlahStok) {
		this.kodeBarang = kodeBarang;
		this.namaBarang = namaBarang;
		this.jumlahStok = jumlahStok;
	}
	
    public void tambahStok(int jumlah) {
        if (jumlah > 0) {
            jumlahStok += jumlah;
        }
    }

    public void kurangiStok(int jumlah) {
        if (jumlah > 0 && jumlah <= jumlahStok) {
        	jumlahStok -= jumlah;
        }
    }
    
    public void setJumlahStok(int jumlahStok) {
        this.jumlahStok = jumlahStok;
    }

	public String getKodeBarang() {return kodeBarang;}
	
	public String getNamaBarang() {return namaBarang;}
	
	public int getJumlahStok() {return jumlahStok;}
	
	public String getInfo() {
		return "Kode Barang: " + kodeBarang + ", Nama Barang: " + namaBarang + ", Jumlah Stok: " + jumlahStok;
	}
	
}
</pre>
</details>

Kode di atas adalah kelas BarangGudang yang merepresentasikan satu objek barang di dalam sistem inventori. Setiap barang memiliki tiga atribut utama, yaitu kode barang, nama barang, dan jumlah stok. Kelas ini digunakan untuk menyimpan dan mengelola informasi setiap barang yang ada di gudang.
Di dalamnya terdapat beberapa metode untuk menambah stok, mengurangi stok, serta mengatur jumlah stok secara langsung. Metode tambahStok dan kurangiStok juga memiliki pengamanan agar stok tidak bertambah atau berkurang dengan nilai yang tidak valid. Selain itu, terdapat metode getter untuk mengambil nilai dari setiap atribut, dan metode getInfo yang menampilkan informasi lengkap tentang barang dalam bentuk teks.

Secara sederhana, kelas ini berfungsi sebagai model data yang menyimpan detail dan operasi dasar dari satu barang di gudang.

## Hasil

![Tampilan Aplikasi](https://github.com/Epikkz/Sistem_Inventori_Gedung_By_Team_8/blob/main/assets/Output.png)

Gambar tersebut menampilkan tampilan utama dari aplikasi Sistem Inventori Gudang yang dibuat dengan JavaFX. Di bagian atas terdapat kolom pencarian untuk mencari barang berdasarkan kode. Di tengah, ada tabel yang menampilkan daftar barang dengan tiga kolom yaitu Kode, Nama Barang, dan Stok.

Tabel tersebut sudah berisi tiga data awal, yaitu Monitor, Keyboard, dan Mouse dengan jumlah stok masing-masing. Di bawah tabel terdapat dua tombol bergambar ikon plus dan minus untuk menambah atau mengurangi stok barang yang dipilih.

Bagian paling bawah berisi kolom input untuk menambahkan barang baru, terdiri dari input kode, nama, stok, serta dua tombol untuk menambah dan menghapus barang. Secara keseluruhan, tampilan ini menunjukkan aplikasi inventori yang rapi, interaktif, dan mudah digunakan untuk mengelola data barang gudang.
