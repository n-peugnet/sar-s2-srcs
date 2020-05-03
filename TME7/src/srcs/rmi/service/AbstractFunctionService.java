package srcs.rmi.service;

import java.io.Serializable;
import java.rmi.RemoteException;

@SuppressWarnings("serial")
public abstract class AbstractFunctionService<P extends Serializable, R extends Serializable> implements FunctionService<P, R>, Cloneable {
	
	protected String name;
	protected FunctionService<P, R> proxy;

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
			return proxy.invoke(param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized FunctionService<P, R> migrateTo(Host host) throws RemoteException {
		if (proxy == null) {
			try {
				proxy = host.deployExistingService(getName(), (FunctionService<P, R>) this.clone());
			} catch (CloneNotSupportedException e) {
				throw new RemoteException("Could not copy the service class", e);
			}
			return proxy;
		} else {
			throw new RemoteException("Already migrated");
		}
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
