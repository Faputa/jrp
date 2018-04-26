package jrp;

public class Protocol
{
	public static class Payload
	{
		public String ClientId;
		public Integer RemotePort;
		public String Error;
	}

	public String Type;
	public Payload Payload;
}
