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
import srcs.rmi.concurrent.SharedVariableReliable;

public class SystemDeployer {
	
	private Process p;
	private SharedVariable<Integer> varClassical;
	private SharedVariable<Integer> varReliable;
	private Registry registry;
	private String classical = "variableIntegerClassical";
	private String reliable = "variableIntegerReliable";
	
	@Before
	public void setUp() throws IOException, AlreadyBoundException, InterruptedException {
		new ProcessBuilder("killall", "-q", "rmiregistry").inheritIO().start().waitFor();
		p = new ProcessBuilder("rmiregistry", "-J-Djava.class.path=" + System.getProperty("java.class.path")).inheritIO().start();
		varClassical = new SharedVariableClassical<>(0);
		varReliable = new SharedVariableReliable<>(0);
		UnicastRemoteObject.exportObject(varClassical, 0);
		UnicastRemoteObject.exportObject(varReliable, 0);
		System.out.println("Waiting for rmi server...");
		Thread.sleep(500);
		registry = LocateRegistry.getRegistry("localhost");
		registry.bind(classical, varClassical);
		registry.bind(reliable, varReliable);
	}
	
	@After
	public void tearDown() throws AccessException, RemoteException, NotBoundException {
		registry.unbind(reliable);
		registry.unbind(classical);
		UnicastRemoteObject.unexportObject(varReliable, true);
		UnicastRemoteObject.unexportObject(varClassical, true);
		p.destroyForcibly();
	}

}
