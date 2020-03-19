package srcs.service.annuaire;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import srcs.service.EtatGlobal;
import srcs.service.Service;

@EtatGlobal
public class AnnuaireService implements Service, Annuaire {

	protected Map<String, String> map = new ConcurrentHashMap<>();

	@Override
	public String lookup(String nom) {
		String valeur = map.get(nom);
		return valeur == null ? "": valeur;
	}

	@Override
	public void bind(String nom, String valeur) {
		map.put(nom, valeur);
	}

	@Override
	public void unbind(String nom) {
		map.remove(nom);
	}

}
