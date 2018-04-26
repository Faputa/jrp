package jrp.client.model;

public class Tunnel
{
	private int remotePort;
	private String localHost;
	private int localPort;

	public int getRemotePort()
	{
		return remotePort;
	}

	public void setRemotePort(int remotePort)
	{
		this.remotePort = remotePort;
	}

	public String getLocalHost()
	{
		return localHost;
	}

	public void setLocalHost(String localHost)
	{
		this.localHost = localHost;
	}

	public int getLocalPort()
	{
		return localPort;
	}

	public void setLocalPort(int localPort)
	{
		this.localPort = localPort;
	}
}
