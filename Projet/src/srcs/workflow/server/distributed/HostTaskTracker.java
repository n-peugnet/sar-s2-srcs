package srcs.workflow.server.distributed;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

import srcs.workflow.job.Job;
import srcs.workflow.server.TaskHost;

public class HostTaskTracker implements TaskHost {

	@Override
	public Object submitTask(Job job, String method, Object[] args) throws RemoteException {
		Class<?>[] classes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			classes[i] = arg.getClass();
		}
		Method m;
		try {
			m = job.getClass().getMethod(method, classes);
		} catch (NoSuchMethodException | SecurityException e) {
			// Should never happen
			throw new RemoteException("Could not find the method to execute", e);
		}
		Object result;
		try {
			result = m.invoke(job, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RemoteException("Failed to execute the task", e);
		}
		return result;
	}

}