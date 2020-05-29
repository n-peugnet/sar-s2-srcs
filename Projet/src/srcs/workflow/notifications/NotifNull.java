package srcs.workflow.notifications;

import java.rmi.RemoteException;

public class NotifNull implements Notifiable {

	@Override
	public void notify(Object notification) throws RemoteException {
		// Do nothing
	}

}
