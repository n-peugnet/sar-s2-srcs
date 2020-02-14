package srcs.persistance;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import srcs.banque.Compte;

public class PersistanceTools {
	
	public static void saveArrayInt(String f, int[] tab) throws PersitanceException {
		try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f)))
		{
			os.writeObject(tab);
		} catch(IOException e) {
			throw new PersitanceException(e);
		}
	}

	public static int[] loadArrayInt(String f) throws PersitanceException {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(f)))
		{
			int[] tab = (int[]) is.readObject();
			return tab;
		} catch(Throwable e) {
			throw new PersitanceException(e);
		}
	}
	
	public static void saveCompte(String f, Compte c) {
		save(f, c);
	}
	
	public static Compte loadCompte(String f) {
		return (Compte) load(f);
	}
	
	public static void save(String f, Serializable s) throws PersitanceException {
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f)))
		{
			os.writeObject(s);
		} catch(Throwable e) {
			throw new PersitanceException(e);
		}
	}

	public static Serializable load(String f) throws PersitanceException {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(f)))
		{
			Serializable s = (Serializable) is.readObject();
			return s;
		} catch(Throwable e) {
			throw new PersitanceException(e);
		}
	}
	
	
}
