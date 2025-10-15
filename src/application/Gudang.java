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
