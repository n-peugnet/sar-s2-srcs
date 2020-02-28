package srcs.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMultithread {
	private final int port;
	private final Class<? extends Service> serviceClass;
	private Service service;

	public ServeurMultithread(int port, Class<? extends Service> serviceClass) {
		this.port = port;
		this.serviceClass = serviceClass;
	}

	public void listen() throws IllegalStateException{
		try (ServerSocket ss = new ServerSocket(port)) {
			while (!Thread.currentThread().isInterrupted()) {
				Socket client = ss.accept();
				Service s = getService();
				new Thread(() -> {
					s.execute(client);
				}).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Service getService() throws IllegalStateException {
		try {
			if (serviceClass.getAnnotation(SansEtat.class) != null) {
				return serviceClass.newInstance();
			} else if (serviceClass.getAnnotation(EtatGlobal.class) != null) {
				if (service == null) {
					service = serviceClass.newInstance();
				}
				return service;
			} else {
				throw new IllegalStateException();
			}
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}
}
