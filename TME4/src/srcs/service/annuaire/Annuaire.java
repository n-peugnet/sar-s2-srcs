package srcs.service.annuaire;

public interface Annuaire {
	public String lookup(String nom);
	public void bind(String nom, String valeur);
	public void unbind(String nom);
}
