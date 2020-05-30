package srcs.workflow.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import srcs.workflow.job.Job;
import srcs.workflow.notifications.Notifiable;

public interface JobHost extends Remote {
	
	/**
	 * Demande à un JobHost d'éxécuter un job.
	 * @param target la cible où envoyer les notifications.
	 * @param job le job à éxécuter.
	 * @return le résultat de l'éxécution du job.
	 * @throws RemoteException si un problème s'est déroulé lors de l'éxecution du Job.
	 */
	public Map<String, Object> submitJob(Notifiable target, Job job) throws RemoteException;
}
