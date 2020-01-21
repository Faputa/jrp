package jrp.server.model;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Request
{
	private Integer remotePort;
	private Socket outerSocket;
	private BlockingQueue<Socket> proxySocketQueue = new ArrayBlockingQueue<Socket>(1);

	public Integer getRemotePort()
	{
		return remotePort;
	}

	public void setRemotePort(Integer remotePort)
	{
		this.remotePort = remotePort;
	}

	public Socket getOuterSocket()
	{
		return outerSocket;
	}

	public void setOuterSocket(Socket outerSocket)
	{
		this.outerSocket = outerSocket;
	}

	public Socket getProxySocket(long timeout, TimeUnit unit) throws InterruptedException
	{
		return proxySocketQueue.poll(timeout, unit);
	}

	public void setProxySocket(Socket proxySocket) throws InterruptedException
	{
		proxySocketQueue.put(proxySocket);
	}
}
