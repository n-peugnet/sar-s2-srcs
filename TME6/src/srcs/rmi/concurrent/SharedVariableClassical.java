package srcs.rmi.concurrent;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;

public class SharedVariableClassical<T extends Serializable> implements SharedVariable<T> {
	protected T object;
	protected final Queue<Thread> queue;
	
	public SharedVariableClassical(T object) throws RemoteException {
		this.object = object;
		this.queue = new LinkedList<>();
	}

	@Override
	public T obtenir() throws RemoteException {
		synchronized (queue) {
			queue.add(Thread.currentThread());
			while (queue.peek() != Thread.currentThread()) {
				try {
					queue.wait();
				} catch (InterruptedException e) {
					throw new RemoteException("Thread interrupted", e);
				}
			}
		}
		return object;
	}

	@Override
	public void relacher(T object) throws RemoteException {
		synchronized (queue) {
			if (queue.peek() == Thread.currentThread()) {
				this.object = object;
				queue.poll();
				queue.notifyAll();
			}
		}
	}
}
