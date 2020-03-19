package srcs.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface Service {
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
				for (Parameter arg : m.getParameters()) {
					Class<?> c = arg.getType();
					if (c.equals(Boolean.TYPE)) {
						args.add(ois.readBoolean());
					} else if (c.equals(Byte.TYPE)) {
						args.add(ois.readByte());
					} else if (c.equals(Character.TYPE)) {
						args.add(ois.readChar());
					} else if (c.equals(Integer.TYPE)) {
						args.add(ois.readInt());
					} else if (c.equals(Long.TYPE)) {
						args.add(ois.readLong());
					} else if (c.equals(Double.TYPE)) {
						args.add(ois.readDouble());
					} else {
						args.add(ois.readObject());
					}
				}
				Object res = m.invoke(this, args.toArray());
				oos.writeObject(res);
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
