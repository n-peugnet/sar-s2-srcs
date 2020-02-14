package srcs;

import java.util.List;

public abstract class Command {
	
	protected List<String> args;
	protected String command;

	public Command(List<String> args) throws IllegalArgumentException {
		this.args = args.subList(1, args.size());
		this.command = args.get(0);
	}

	public abstract void execute();
}
