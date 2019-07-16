package control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		File f = new File("src/prueba.xml");
		FileInputStream fi = new FileInputStream(f);
		File fo = new File("src/out.xml");
		FileOutputStream fos = new FileOutputStream(fo);
		MapeoCrc mapeocrc = new MapeoCrc();
		mapeocrc.executeIn(fi,fos);

	}

}
