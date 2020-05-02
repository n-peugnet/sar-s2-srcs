package srcs.rmi.service;

import java.io.Serializable;
import java.rmi.RemoteException;

@SuppressWarnings("serial")
public abstract class AbstractFunctionService<P extends Serializable, R extends Serializable> implements FunctionService<P, R> {
	
	protected String name;
	protected AbstractFunctionService<P, R> proxy;

	protected abstract R perform(P param) throws RemoteException;

	public AbstractFunctionService(String name) {
		this.name = name;
	}

	@Override
	public String getName() throws RemoteException {
		if (proxy == null)
			return name;
		else
			return proxy.getName();
	}

	@Override
	public synchronized R invoke(P param) throws RemoteException {
		if (proxy == null)
			return perform(param);
		else
			return proxy.perform(param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized FunctionService<P, R> migrateTo(Host host) throws RemoteException {
		if (proxy == null) {
			try {
				proxy = this.getClass().getConstructor(String.class).newInstance(getName());
			} catch (ReflectiveOperationException e) {
				throw new RemoteException("Could not copy the service class", e);
			}
			return host.deployExistingService(getName(), proxy);
		} else {
			throw new RemoteException("Already migrated");
		}
	}
	
	
}
