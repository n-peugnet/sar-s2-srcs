package srcs.service.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import srcs.service.ServeurMultithread;
import srcs.service.annuaire.Annuaire;
import srcs.service.annuaire.AnnuaireAppelDistant;
import srcs.service.annuaire.AnnuaireProxy;
import srcs.service.calculatrice.Calculatrice;
import srcs.service.calculatrice.Calculatrice.ResDiv;
import srcs.service.calculatrice.CalculatriceAppelDistant;
import srcs.service.calculatrice.CalculatriceProxy;

public class TestAppelDistant {

	public static int portannuaire=4235;
	public static int portcalculette=14235;
	
	private Thread annuaire;
	private Thread calculette;
	
	
	@Before
	public void setUp() throws Exception {
		annuaire =new Thread( () -> new ServeurMultithread(portannuaire, AnnuaireAppelDistant.class).listen());
		calculette =new Thread( () -> new ServeurMultithread(portcalculette, CalculatriceAppelDistant.class).listen());

		annuaire.start();
		calculette.start();
		Thread.sleep(200);
	}

	@After
	public void tearDown() throws Exception {
		annuaire.interrupt();
		calculette.interrupt();
		portannuaire++;
		portcalculette++;
	}

	@Test
	public void testCalculatrice() {
		Calculatrice calc = new CalculatriceProxy("localhost", portcalculette);
		
		assertEquals(0, calc.add(4, -4));
		assertEquals(9, calc.add(4,5));
		
		assertEquals(3, calc.sous(4, 1));
		assertEquals(8, calc.sous(4, -4));
		
		assertEquals(12, calc.mult(3, 4));
		assertEquals(-16, calc.mult(4, -4));
		
		ResDiv res = calc.div(5, 3);
		assertEquals(1, res.getQuotient());
		assertEquals(2, res.getReste());
		
		
	}
	
	@Test
	public void testAnnuaire() {
		String nom = "nomtest";
		String value="valeurtest";
		Annuaire annuaire = new AnnuaireProxy("localhost", portannuaire);
		annuaire.bind(nom, value);
		assertEquals(value, annuaire.lookup(nom));
		annuaire.unbind(nom);
		assertEquals("", annuaire.lookup(nom));
		
	}

}
