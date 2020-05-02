package srcs.rmi.service;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
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
			return deployExistingService(name, cls.newInstance());
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
		services.put(name, service);
		return service;
	}

	@Override
	public boolean undeployService(String name) throws RemoteException {
		if (services.containsKey(name)) {
			services.remove(name);
			return true;
		}
		return false;
	}

	@Override
	public Set<String> getServices() throws RemoteException {
		return services.keySet();
	}

}
