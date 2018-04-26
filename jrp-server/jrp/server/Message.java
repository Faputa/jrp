package jrp.server;

import jrp.Protocol;
import jrp.Protocol.Payload;
import jrp.util.GsonUtil;

public class Message
{
	private Message()
	{
	}

	public static String AuthResp(String ClientId)
	{
		Payload payload = new Payload();
		payload.ClientId = ClientId;
		payload.Error = "";
		Protocol protocol = new Protocol();
		protocol.Type = "AuthResp";
		protocol.Payload = payload;
		return GsonUtil.toJson(protocol);
	}

	public static String NewTunnel(Integer RemotePort, String Error)
	{
		Payload payload = new Payload();
		payload.RemotePort = RemotePort;
		payload.Error = Error;
		Protocol protocol = new Protocol();
		protocol.Type = "NewTunnel";
		protocol.Payload = payload;
		return GsonUtil.toJson(protocol);
	}

	public static String ReqProxy()
	{
		Payload payload = new Payload();
		Protocol protocol = new Protocol();
		protocol.Type = "ReqProxy";
		protocol.Payload = payload;
		return GsonUtil.toJson(protocol);
	}

	public static String StartProxy(Integer RemotePort)
	{
		Payload payload = new Payload();
		payload.RemotePort = RemotePort;
		Protocol protocol = new Protocol();
		protocol.Type = "StartProxy";
		protocol.Payload = payload;
		return GsonUtil.toJson(protocol);
	}

	public static String Pong()
	{
		Payload payload = new Payload();
		Protocol protocol = new Protocol();
		protocol.Type = "Pong";
		protocol.Payload = payload;
		return GsonUtil.toJson(protocol);
	}
}
