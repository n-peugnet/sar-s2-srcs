package srcs.persistance.test;

import static org.junit.Assert.*;
import static srcs.persistance.PersistanceTools.*;

import org.junit.Test;

import srcs.banque.Compte;
import srcs.persistance.PersitanceException;

import java.io.Serializable;
public class SauvegardableTest {
		
	@Test
	public void testSauvegardable() {
		try {
			String fichier = "/tmp/save";
			Compte cpt = new Compte("cpt1");
			cpt.crediter(5.0);
			cpt.debiter(2.0);
			assertEquals(3,cpt.getSolde(),0);
			save(fichier, cpt);
			Serializable bis = load(fichier);
			assertTrue(bis instanceof Compte);
			assertEquals(cpt, bis);
			assertEquals(3,((Compte)bis).getSolde(),0);
		}catch(PersitanceException e) {
			assertTrue(false);
		}
	}

}
