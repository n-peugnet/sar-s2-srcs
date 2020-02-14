package srcs.banque;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Banque implements Serializable {

	private static final long serialVersionUID = -780817412817667781L;
	private final Set<Client> clients;
	
	public Banque() {
		clients=new HashSet<>();
	}
		
	public int nbClients() {
		return clients.size();
	}
	
	public int nbComptes() {
		Set<Compte> comptes = new HashSet<>();
		for(Client c : clients) {
			comptes.add(c.getCompte());
		}
		return comptes.size();
	}
	
	public Client getClient(String nom) {
		for(Client c : clients) {
			if(c.getNom().equals(nom)) return c;
		}
		return null;
	}
	
	public boolean addNewClient(Client c) {
		return clients.add(c);
	}
	

}
