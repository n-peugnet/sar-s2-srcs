package srcs.rmi.service;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface Host extends Remote {

	public <P extends Serializable, R extends Serializable> FunctionService<P, R> deployNewService(String name, Class<? extends FunctionService<P, R>> cls) throws RemoteException;
	public <P extends Serializable, R extends Serializable> FunctionService<P, R>  deployExistingService(String name, FunctionService<P, R> service) throws RemoteException;
	public void undeployService(String name) throws RemoteException;
	public Set<String> getServices() throws RemoteException;
}
