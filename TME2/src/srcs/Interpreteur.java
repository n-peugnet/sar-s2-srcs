package srcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreteur {
	
	protected Map<String, Class<? extends Command>> commands = new HashMap<String, Class<? extends Command>>();
	
	public Interpreteur() {
	    commands.put("echo", Echo.class);
	    commands.put("exit", Exit.class);
	    commands.put("deploy", Deploy.class);
	    commands.put("undeploy", Undeploy.class);
	}
	
	void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				String[] pieces = line.split(" ");
				String command = pieces[0];
				try {
					if (!this.commands.containsKey(command)) {
						throw new IllegalArgumentException("Unknown command: " + command);
					}
					Class<? extends Command> cls = this.commands.get(command);
					Constructor<? extends Command> cons;
					Command c;
					if (cls.isMemberClass()) {
						cons = cls.getConstructor(this.getClass(), List.class);
						c = cons.newInstance(this, Arrays.asList(pieces));
					} else {
						cons = cls.getConstructor(List.class);
						c = cons.newInstance(Arrays.asList(pieces));
					}
					c.execute();
				} catch (IllegalArgumentException e) {
					System.err.println(e.getMessage());
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class Deploy extends Command {

		String command;
		String path;
		String name;
		Class<? extends Command> cls;
		
		public Deploy(List<String> args) throws IllegalArgumentException {
			super(args);
			if (this.args.size() < 3) {
				throw new IllegalArgumentException("Not enough arguments");
			}
			command = this.args.get(0);
			path = this.args.get(1);
			name =this.args.get(2);
			try {
				URL url = new File(path).toURI().toURL();
				ClassLoader loader = new URLClassLoader(new URL[]{url});
				loader.loadClass(name);
				cls = (Class<Command>) Class.forName(name);
			} catch (ClassNotFoundException|MalformedURLException e) {
				throw new IllegalArgumentException(e);
			}
		}

		@Override
		public void execute() {
			Interpreteur.this.commands.put(command, cls);
			
		}
	}
	
	class Undeploy extends Command {
		String command;
		
		public Undeploy(List<String> args) throws IllegalArgumentException {
			super(args);
			this.command = this.args.get(0); 
		}

		@Override
		public void execute() {
			Interpreteur.this.commands.remove(command);
		}
	}
}
