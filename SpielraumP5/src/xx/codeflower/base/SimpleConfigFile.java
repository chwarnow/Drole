package xx.codeflower.base;

import java.io.IOException;
import java.util.ArrayList;

public class SimpleConfigFile {

	public static ArrayList<String> parse(String filename) throws IOException {
		ArrayList<String> ini = new ArrayList<String>();
		
		ArrayList<String> lines = TextFileReader.read(filename);
		
		return ini;
	}

	
}
