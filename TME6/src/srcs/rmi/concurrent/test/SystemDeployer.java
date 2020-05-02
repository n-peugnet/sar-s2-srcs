package srcs.rmi.concurrent.test;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.junit.After;
import org.junit.Before;

import srcs.rmi.concurrent.SharedVariable;
import srcs.rmi.concurrent.SharedVariableClassical;

public class SystemDeployer {
	
	private Process p;
	private SharedVariable<Integer> var;
	private Registry registry;
	private String name = "variableIntegerClassical";
	
	@Before
	public void setUp() throws IOException, AlreadyBoundException, InterruptedException {
		new ProcessBuilder("killall", "-q", "rmiregistry").inheritIO().start().waitFor();
		p = new ProcessBuilder("rmiregistry", "-J-Djava.class.path=" + System.getProperty("java.class.path")).inheritIO().start();
		var = new SharedVariableClassical<>(0);
		UnicastRemoteObject.exportObject(var, 0);
		System.out.println("Waiting for rmi server...");
		Thread.sleep(1000);
		registry = LocateRegistry.getRegistry("localhost");
		registry.bind(name, var);
	}
	
	@After
	public void tearDown() throws AccessException, RemoteException, NotBoundException {
		registry.unbind(name);
		UnicastRemoteObject.unexportObject(var, true);
		p.destroyForcibly();
	}

}
