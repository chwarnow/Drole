package xx.codeflower.base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TextFileReader {

	public static ArrayList<String> read(String filename) throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
	    try {
	        String line = br.readLine();

	        while(line != null) {
	        	lines.add(line);
	            line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
	    
	    return lines;
	}

}
