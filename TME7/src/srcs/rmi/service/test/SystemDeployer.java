package srcs.rmi.service.test;

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

import srcs.rmi.service.Host;
import srcs.rmi.service.SuperHost;

public class SystemDeployer {
	
	private Process p;
	private Host host1;
	private Host host2;
	private Registry registry;
	private String name1 = "host1";
	private String name2 = "host2";
	
	@Before
	public void setUp() throws IOException, AlreadyBoundException, InterruptedException {
		new ProcessBuilder("killall", "-q", "rmiregistry").inheritIO().start().waitFor();
		p = new ProcessBuilder("rmiregistry", "-J-Djava.class.path=" + System.getProperty("java.class.path")).inheritIO().start();
		host1 = new SuperHost();
		host2 = new SuperHost();
		UnicastRemoteObject.exportObject(host1, 0);
		UnicastRemoteObject.exportObject(host2, 0);
		System.out.println("Waiting for rmi server...");
		Thread.sleep(500);
		registry = LocateRegistry.getRegistry("localhost");
		registry.bind(name1, host1);
		registry.bind(name2, host2);
	}
	
	@After
	public void tearDown() throws AccessException, RemoteException, NotBoundException {
		registry.unbind(name2);
		registry.unbind(name1);
		UnicastRemoteObject.unexportObject(host2, true);
		UnicastRemoteObject.unexportObject(host1, true);
		p.destroyForcibly();
	}

}
