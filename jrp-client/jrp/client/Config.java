package jrp.client;

import java.util.ArrayList;
import java.util.List;

import jrp.client.model.Tunnel;

public class Config
{
	public List<Tunnel> tunnelList = new ArrayList<Tunnel>();
	public String serverHost = "";
	public int serverPort = 4443;
	public String authToken = "";
	public boolean enableLog = true;
	public long pingTime = 60000;
}
