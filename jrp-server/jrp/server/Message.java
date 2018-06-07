package jrp.server;

import jrp.Protocol;
import jrp.util.GsonUtil;

public class Message
{
	private Message()
	{
	}

	public static String AuthResp(String clientId, String error)
	{
		Protocol protocol = new Protocol();
		protocol.Type = "AuthResp";
		protocol.ClientId = clientId;
		protocol.Error = error;
		return GsonUtil.toJson(protocol);
	}

	public static String NewTunnel(Integer remotePort, String error)
	{
		Protocol protocol = new Protocol();
		protocol.Type = "NewTunnel";
		protocol.RemotePort = remotePort;
		protocol.Error = error;
		return GsonUtil.toJson(protocol);
	}

	public static String ReqProxy()
	{
		Protocol protocol = new Protocol();
		protocol.Type = "ReqProxy";
		return GsonUtil.toJson(protocol);
	}

	public static String StartProxy(Integer remotePort)
	{
		Protocol protocol = new Protocol();
		protocol.Type = "StartProxy";
		protocol.RemotePort = remotePort;
		return GsonUtil.toJson(protocol);
	}

	public static String Pong()
	{
		Protocol protocol = new Protocol();
		protocol.Type = "Pong";
		return GsonUtil.toJson(protocol);
	}
}
