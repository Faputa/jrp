package jrp.server;

import jrp.log.Logger;
import jrp.log.LoggerImpl;
import jrp.server.listener.ClientListener;
import jrp.util.GsonUtil;
import jrp.util.Util;

public class Server
{
	private Context context = new Context();

	public void setPort(int port)
	{
		context.setPort(port);
	}

	public void setToken(String token)
	{
		context.setToken(token);
	}

	public void setLog(Logger log)
	{
		context.setLog(log);
	}

	public void start()
	{
		try
		{
			Thread clientListenerThread = new Thread(new ClientListener(context));
			clientListenerThread.setDaemon(true);
			clientListenerThread.start();
			while(true)
			{
				try{Thread.sleep(5000);}catch(InterruptedException e){}
			}
		}
		catch(Exception e)
		{
			e.getStackTrace();
		}
	}

	public static void main(String[] args)
	{
		String json = Util.readTextFile(Util.getLocation("resource/server.json"));
		Config config = GsonUtil.toBean(json, Config.class);

		System.setProperty("javax.net.ssl.keyStore", Util.getLocation(config.sslKeyStore));
		System.setProperty("javax.net.ssl.keyStorePassword", config.sslKeyStorePassword);

		Server server = new Server();
		server.setPort(config.port);
		server.setToken(config.token);
		server.setLog(new LoggerImpl().setEnableLog(config.enableLog));
		server.start();
	}
}
