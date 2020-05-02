package srcs.rmi.service;

import java.io.Serializable;
import java.rmi.RemoteException;

public abstract class AbstractFunctionService<P extends Serializable, R extends Serializable> implements FunctionService<P, R> {
	
	protected String name;

	protected abstract R perform(P param) throws RemoteException;

	public AbstractFunctionService(String name) {
		this.name = name;
	}

	@Override
	public String getName() throws RemoteException {
		return name;
	}

	@Override
	public R invoke(P param) throws RemoteException {
		return perform(param);
	}

	@Override
	public FunctionService<P, R> migrateTo(Host host) throws RemoteException {
		return host.deployExistingService(getName(), this);
	}
	
	
}
