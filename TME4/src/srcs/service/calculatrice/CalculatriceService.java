package srcs.service.calculatrice;

import srcs.service.SansEtat;
import srcs.service.Service;

@SansEtat
public class CalculatriceService implements Calculatrice, Service {

	@Override
	public int add(int a, int b) {
		return a + b;
	}

	@Override
	public int sous(int a, int b) {
		return a - b;
	}

	@Override
	public int mult(int a, int b) {
		return a * b;
	}

	@Override
	public ResDiv div(int a, int b) {
		return new ResDiv(a / b, a % b);
	}

}
