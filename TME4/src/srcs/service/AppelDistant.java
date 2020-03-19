package srcs.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface AppelDistant extends Service {
	public default void execute(Socket connection) {
		try (
			ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
		) {
			try {
				String method = ois.readUTF();
				Optional<Method> om = Arrays.stream(this.getClass().getMethods())
					.filter((Method m) -> m.getName().contentEquals(method))
					.findFirst();
				Method m = om.get();
				List<Object> args = new ArrayList<>();
				for (int i = 0; i < m.getParameterCount(); i++) {
					args.add(ois.readObject());
				}
				Object res = m.invoke(this, args.toArray());
				if (m.getReturnType().equals(Void.TYPE)) {
					oos.writeObject(new VoidResponse());
				} else {
					oos.writeObject(res);
				}
			} catch (ReflectiveOperationException e) {
				oos.writeObject(new MyProtocolException("Reflexion exception", e));
			} catch (NoSuchElementException e) {
				oos.writeObject(new MyProtocolException("No such method", e));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
