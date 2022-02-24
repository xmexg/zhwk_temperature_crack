import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FILE {
	
	public static void write(String filename, String text) throws IOException {
		File file = new File(filename);
		if(!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream f1 = new FileOutputStream(file,true);
		byte[] buff=text.getBytes();
		f1.write(buff);
		f1.close();
	}
}
