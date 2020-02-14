package srcs;

import java.util.List;
import java.util.stream.Collectors;

public class Echo extends Command {
	

	public Echo(List<String> args) {
		super(args);
	}

	@Override
	public void execute() {
			System.out.println(this.args.stream().collect(Collectors.joining(" ")));
	}
}
