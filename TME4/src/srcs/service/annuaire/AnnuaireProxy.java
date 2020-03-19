package srcs.service.annuaire;

import srcs.service.ClientProxy;

public class AnnuaireProxy extends ClientProxy implements Annuaire {

	public AnnuaireProxy(String host, int port) {
		super(host, port);
	}

	@Override
	public String lookup(String nom) {
		return (String) invokeService("lookup", nom);
	}

	@Override
	public void bind(String nom, String valeur) {
		invokeService("bind", nom, valeur);

	}

	@Override
	public void unbind(String nom) {
		invokeService("unbind", nom);

	}

}
