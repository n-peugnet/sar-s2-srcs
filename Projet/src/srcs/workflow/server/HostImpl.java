package srcs.workflow.server;

import java.rmi.RemoteException;
import java.util.Map;

import srcs.workflow.executor.JobExecutorParallel;
import srcs.workflow.executor.JobExecutorPluggable;
import srcs.workflow.job.Job;
import srcs.workflow.job.ValidationException;
import srcs.workflow.notifications.Notifiable;

public class HostImpl implements JobHost {

	@Override
	public Map<String, Object> submitJob(Notifiable target, Job job) throws RemoteException {
		JobExecutorPluggable executor = new JobExecutorParallel(job);
		return executeJob(executor, target, job);
	}

	/**
	 * Fonction interne permettant d'effectivement execute un Job via le JobExecutor donné.
	 * @param executor l'executor avec lequel on veut executer le job.
	 * @param target la cible où envoyer les notifications.
	 * @param job le job à éxécuter.
	 * @return le résultat de l'éxécution du job.
	 * @throws RemoteException si un problème s'est déroulé lors de l'éxecution du Job.
	 */
	protected Map<String, Object> executeJob(JobExecutorPluggable executor, Notifiable target, Job job) throws RemoteException {
		try {
			return executor.execute(target);
		} catch (ValidationException e) {
			throw new RemoteException("Provided job is not valid", e);
		} catch (Exception e) {
			throw new RemoteException("The execution of this job failed", e);
		}
	}
}
