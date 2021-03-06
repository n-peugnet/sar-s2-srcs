package srcs.rmi.service;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SuperHost implements Host {
	
	Map<String, FunctionService<? extends Serializable, ? extends Serializable>> services;
	
	public SuperHost() {
		services = new HashMap<>();
	}

	@Override
	public <P extends Serializable, R extends Serializable> FunctionService<P, R> deployNewService(
		String name,
		Class<? extends FunctionService<P, R>> cls)
		throws RemoteException
	{
		try {
			return deployExistingService(name, cls.getConstructor(String.class).newInstance(name));
		} catch (ReflectiveOperationException e) {
			throw new RemoteException("Could not instanciate the service class", e);
		}
		
	}

	@Override
	public <P extends Serializable, R extends Serializable> FunctionService<P, R> deployExistingService(
		String name,
		FunctionService<P, R> service)
		throws RemoteException
	{
		if (services.containsKey(name)) {
			throw new RemoteException("Service '" + name + "' already exists");
		}
		UnicastRemoteObject.exportObject(service, 0);
		services.put(name, service);
		return service;
	}

	@Override
	public boolean undeployService(String name) throws RemoteException {
		if (services.containsKey(name)) {
			UnicastRemoteObject.unexportObject(services.remove(name), true);
			return true;
		}
		return false;
	}

	@Override
	public Set<String> getServices() throws RemoteException {
		return new HashSet<>(services.keySet());
	}

}
