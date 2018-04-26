package jrp.server;

import jrp.Protocol;
import jrp.util.GsonUtil;

public class Message
{
	private Message()
	{
	}

	public static String AuthResp(String ClientId)
	{
		Protocol protocol = new Protocol();
		protocol.Type = "AuthResp";
		protocol.ClientId = ClientId;
		protocol.Error = "";
		return GsonUtil.toJson(protocol);
	}

	public static String NewTunnel(Integer RemotePort, String Error)
	{
		Protocol protocol = new Protocol();
		protocol.Type = "NewTunnel";
		protocol.RemotePort = RemotePort;
		protocol.Error = Error;
		return GsonUtil.toJson(protocol);
	}

	public static String ReqProxy()
	{
		Protocol protocol = new Protocol();
		protocol.Type = "ReqProxy";
		return GsonUtil.toJson(protocol);
	}

	public static String StartProxy(Integer RemotePort)
	{
		Protocol protocol = new Protocol();
		protocol.Type = "StartProxy";
		protocol.RemotePort = RemotePort;
		return GsonUtil.toJson(protocol);
	}

	public static String Pong()
	{
		Protocol protocol = new Protocol();
		protocol.Type = "Pong";
		return GsonUtil.toJson(protocol);
	}
}
