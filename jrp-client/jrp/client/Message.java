package jrp.client;

import jrp.Protocol;
import jrp.Protocol.Payload;
import jrp.client.model.Tunnel;
import jrp.util.GsonUtil;

public class Message
{
	private Message()
	{
	}

	public static String Auth()
	{
		Payload payload = new Payload();
		payload.ClientId = "";
		Protocol protocol = new Protocol();
		protocol.Type = "Auth";
		protocol.Payload = payload;
		return GsonUtil.toJson(protocol);
	}

	public static String ReqTunnel(Tunnel tunnel)
	{
		Payload payload = new Payload();
		payload.RemotePort = tunnel.getRemotePort();
		Protocol protocol = new Protocol();
		protocol.Type = "ReqTunnel";
		protocol.Payload = payload;
		return GsonUtil.toJson(protocol);
	}

	public static String RegProxy(String ClientId)
	{
		Payload payload = new Payload();
		payload.ClientId = ClientId;
		Protocol protocol = new Protocol();
		protocol.Type = "RegProxy";
		protocol.Payload = payload;
		return GsonUtil.toJson(protocol);
	}

	public static String Ping()
	{
		Payload payload = new Payload();
		Protocol protocol = new Protocol();
		protocol.Type = "Ping";
		protocol.Payload = payload;
		return GsonUtil.toJson(protocol);
	}
}
