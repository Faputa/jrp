package jrp.client;

import java.net.Socket;
import java.util.List;

import jrp.client.connect.ControlConnect;
import jrp.client.model.Tunnel;
import jrp.log.Logger;
import jrp.log.LoggerImpl;
import jrp.socket.SocketHelper;
import jrp.util.GsonUtil;
import jrp.util.Util;

public class Client
{
	private Context context = new Context();
	private Logger log = context.getLog();
	private long pingTime = 60000;// 心跳包周期默认为一分钟

	public void setServerHost(String serverHost)
	{
		context.setServerHost(serverHost);
	}

	public void setServerPort(int serverPort)
	{
		context.setServerPort(serverPort);
	}

	public void setAuthToken(String authToken)
	{
		context.setAuthToken(authToken);
	}

	public void setTunnelList(List<Tunnel> tunnelList)
	{
		context.setTunnelList(tunnelList);
	}

	public void setLog(Logger log)
	{
		context.setLog(log);
	}

	public void setPingTime(long pingTime)
	{
		this.pingTime = pingTime;
	}

	public void start()
	{
		try(Socket socket = SocketHelper.newSSLSocket(context.getServerHost(), context.getServerPort()))
		{
			Thread thread = new Thread(new ControlConnect(socket, context));
			thread.setDaemon(true);
			thread.start();
			while(true)
			{
				try{Thread.sleep(this.pingTime);}catch(InterruptedException e){}
				SocketHelper.sendpack(socket, Message.Ping());
			}
		}
		catch(Exception e)
		{
			log.err(e.getMessage());
		}
	}

	public static void main(String[] args)
	{
		String json = Util.readTextFile(Util.getLocation("resource/client.json"));
		Config config = GsonUtil.toBean(json, Config.class);
		Client client = new Client();
		client.setTunnelList(config.tunnelList);
		client.setServerHost(config.serverHost);
		client.setServerPort(config.serverPort);
		client.setAuthToken(config.authToken);
		client.setPingTime(config.pingTime);
		client.setLog(new LoggerImpl().setEnableLog(config.enableLog));
		client.start();
	}
}
