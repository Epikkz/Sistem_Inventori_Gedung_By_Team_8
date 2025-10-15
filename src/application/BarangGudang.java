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