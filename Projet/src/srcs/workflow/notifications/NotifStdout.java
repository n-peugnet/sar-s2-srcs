package srcs.workflow.notifications;

import java.rmi.RemoteException;

public class NotifStdout implements Notifiable {

	@Override
	public void notify(Object notification) throws RemoteException {
		System.out.println(notification);
	}

}
