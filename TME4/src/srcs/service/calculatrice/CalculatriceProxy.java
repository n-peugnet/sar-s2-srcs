package srcs.service.calculatrice;

import srcs.service.ClientProxy;

public class CalculatriceProxy extends ClientProxy implements Calculatrice {

	public CalculatriceProxy(String host, int port) {
		super(host, port);
	}

	@Override
	public int add(int a, int b) {
		return (Integer) invokeService("add", a, b);
	}

	@Override
	public int sous(int a, int b) {
		return (Integer) invokeService("sous", a, b);
	}

	@Override
	public int mult(int a, int b) {
		return (Integer) invokeService("mult", a, b);
	}

	@Override
	public ResDiv div(int a, int b) {
		return (ResDiv) invokeService("div", a, b);
	}

}
