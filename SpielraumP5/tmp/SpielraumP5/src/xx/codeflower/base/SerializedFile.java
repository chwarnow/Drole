package xx.codeflower.base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class SerializedFile<E> {
	
	public void save(Serializable o, String filename) {
		try {
			// use buffering
			OutputStream file = new FileOutputStream(filename);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				output.writeObject(o);
			} finally {
				output.close();
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public E load(String filename) {
		try {
			// use buffering
			InputStream file = new FileInputStream(filename);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			try {
				// deserialize the world
				return (E) input.readObject();
			} finally {
				input.close();
			}
		} catch (ClassNotFoundException ex) {
			System.err.println(ex);
		} catch (IOException ex) {
			System.err.println(ex);
		}
		
		return null;
	}
	
}
