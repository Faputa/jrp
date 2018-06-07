package jrp.client;

import jrp.Protocol;
import jrp.client.model.Tunnel;
import jrp.util.GsonUtil;

public class Message
{
	private Message()
	{
	}

	public static String Auth(String authToken)
	{
		Protocol protocol = new Protocol();
		protocol.Type = "Auth";
		protocol.AuthToken = authToken;
		return GsonUtil.toJson(protocol);
	}

	public static String ReqTunnel(Tunnel tunnel)
	{
		Protocol protocol = new Protocol();
		protocol.Type = "ReqTunnel";
		protocol.RemotePort = tunnel.getRemotePort();
		return GsonUtil.toJson(protocol);
	}

	public static String RegProxy(String ClientId)
	{
		Protocol protocol = new Protocol();
		protocol.Type = "RegProxy";
		protocol.ClientId = ClientId;
		return GsonUtil.toJson(protocol);
	}

	public static String Ping()
	{
		Protocol protocol = new Protocol();
		protocol.Type = "Ping";
		return GsonUtil.toJson(protocol);
	}
}
