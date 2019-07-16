package modelo;

public class TJSON {
	public String Key = null;
	public String Valor = null;

	public TJSON(String Key, String valor) {
		this.Key = Key;
		this.Valor = valor;
	}

	public TJSON() {

	}

	public void set(String Key, String valor) {
		this.Key = Key;
		this.Valor = valor;
	}
	public String getKey() {
		return Key;
	}

	public String getValor() {
		return Valor;
	}



	public String toString() {
		return "La llave es "+Key+" => "+Valor;

	}
}
