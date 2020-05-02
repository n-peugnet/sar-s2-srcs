package srcs.rmi.concurrent;

import java.io.Serializable;
import java.rmi.RemoteException;

public class SharedVariableReliable<T extends Serializable> extends SharedVariableClassical<T> {
	
	private Thread timeout;
	private int duration = 500;

	public SharedVariableReliable(T object) throws RemoteException {
		super(object);
	}
	
	private void rearmTimeout() {
		timeout = new Thread(() -> {
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				return;
			}
			synchronized (queue) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				queue.poll();
				queue.notifyAll();
			}
		});
		timeout.start();
	}
	
	@Override
	public T obtenir() throws RemoteException {
		synchronized (queue) {
			T var = super.obtenir();
			rearmTimeout();
			return var;
		}
	}

	@Override
	public void relacher(T object) throws RemoteException {
		synchronized (queue) {
			timeout.interrupt();
			super.relacher(object);
		}
	}
}
