package srcs.rmi.service;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface FunctionService<P extends Serializable, R extends Serializable> extends Serializable {

	public String getName() throws RemoteException;
	public R invoke(P param) throws RemoteException;
	public FunctionService<P, R> migrateTo(Host host) throws RemoteException;
}
