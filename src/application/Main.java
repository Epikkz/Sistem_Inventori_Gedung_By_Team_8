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
//01001001 00100000 01100011 01101111 01100100 
//01100101 00100000 01101001 01110100 00101100
//00100000 01000101 01110000 01101001 01101011