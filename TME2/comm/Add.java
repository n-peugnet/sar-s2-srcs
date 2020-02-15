import java.util.List;
import srcs.Command;

public class Add extends Command {
	private final int a;
	private final int b;

	public Add(List<String> args) throws IllegalArgumentException {
		super(args);
		if (args.size() < 3) {
			throw new IllegalArgumentException("Usage: add <operande1> <operande2>");
		}
		this.a = Integer.parseInt(args.get(1));
		this.b = Integer.parseInt(args.get(2));
	}

	@Override
	public void execute() {
		System.out.println(a + b);
	}

}
