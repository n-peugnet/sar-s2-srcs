package srcs.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class ClientProxy {
	private final String host;
	private final int port;
	
	public ClientProxy(String host, int port) {
		this.host = host;
		this.port = port;
		
	}
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	protected Object invokeService(String name, Object ...params) {
		try(Socket service = new Socket(host, port)){
			try(ObjectOutputStream oos = new ObjectOutputStream(service.getOutputStream())){
				try(ObjectInputStream ois = new ObjectInputStream(service.getInputStream())){
					oos.writeUTF(name);
					for (Object p : params)
						oos.writeObject(p);
					oos.flush();
					return ois.readObject();
				} catch (ClassNotFoundException e) {
					throw new MyProtocolException(e);
				}
			}
		} catch (IOException e) {
			throw new MyProtocolException(e);
		}
	}

}
