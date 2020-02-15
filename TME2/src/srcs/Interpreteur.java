package srcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreteur {
	
	protected Map<String, Class<? extends Command>> commands = new HashMap<String, Class<? extends Command>>();
	
	public Interpreteur() {
	    commands.put("save", Save.class);
	    commands.put("exit", Exit.class);
	    commands.put("deploy", Deploy.class);
	    commands.put("undeploy", Undeploy.class);
	}
	
	@SuppressWarnings("unchecked")
	public Interpreteur(String file) {
		this();
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
			Object o = is.readObject();
			commands.putAll((HashMap<String, Class<? extends Command>>) o);
		} catch (FileNotFoundException e) {
			System.out.println("No save file found");
		} catch (IOException | ClassCastException | ClassNotFoundException e) {
			e.printStackTrace();
		}
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
					Throwable cause = e.getCause();
					if (cause != null) {
						System.err.println(cause.getMessage());
					} else {
						e.printStackTrace();
					}
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
				throw new IllegalArgumentException("Usage: deploy <command> <dir/jar> <class>");
			}
			command = this.args.get(0);
			path = this.args.get(1);
			name =this.args.get(2);
			if (Interpreteur.this.commands.containsKey(command)) {
				throw new IllegalArgumentException("This command allready exists: " + command);
			}
			try (URLClassLoader loader =
				new URLClassLoader(new URL[] { new File(path).toURI().toURL() })
			) {
				cls = loader.loadClass(name).asSubclass(Command.class);
			} catch (ClassCastException | ClassNotFoundException | IOException e) {
				throw new IllegalArgumentException(e);
			}
		}

		@Override
		public void execute() {
			Interpreteur.this.commands.put(command, cls);
			System.out.println("Added command: " + command);
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
			System.out.println("Removed command: " + command);
		}
	}
	
	class Save extends Command {
		
		String file;

		public Save(List<String> args) throws IllegalArgumentException {
			super(args);
			if (this.args.size() != 1) {
				throw new IllegalArgumentException("Usage: save <file>");
			}
			file = this.args.get(0);
		}

		@Override
		public void execute() {
			try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
				os.writeObject(Interpreteur.this.commands);
				System.out.println("Saved deployed commands in file: " + file);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		
	}
}
