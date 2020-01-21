package jrp.client;

import java.util.List;

import jrp.client.model.Tunnel;

public class Context
{
	public String serverHost;
	public int serverPort;
	public List<Tunnel> tunnelList;
	public String authToken;
}
