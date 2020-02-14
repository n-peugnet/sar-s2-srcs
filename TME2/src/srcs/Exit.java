package srcs;

import java.util.List;

public class Exit extends Command {

	protected int value = 0;

	public Exit(List<String> args) {
		super(args);
		if (this.args.size() > 1) {
			throw new IllegalArgumentException("Topo much args");
		}
		if (this.args.size() == 1) {
			String v = this.args.get(0);
			try {
				this.value = new Integer(v);
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid value: " + v, e);
			}
		}
	}

	@Override
	public void execute() {
		System.out.println("Fin");
		System.exit(this.value);
	}

}
