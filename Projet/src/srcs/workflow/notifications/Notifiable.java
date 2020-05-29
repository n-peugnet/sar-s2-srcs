package srcs.workflow.notifications;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Notifiable extends Remote {
	public void notify(Object notification)throws RemoteException;
}
