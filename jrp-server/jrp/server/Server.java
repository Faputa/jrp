package jrp.server;

import jrp.log.Logger;
import jrp.log.LoggerImpl;
import jrp.server.listener.ClientListener;
import jrp.util.FileUtil;
import jrp.util.GsonUtil;

public class Server
{
	private Context context = new Context();

	public void setPort(int port)
	{
		context.port = port;
	}

	public void setTimeout(int timeout)
	{
		context.timeout = timeout;
	}

	public void setToken(String token)
	{
		context.token = token;
	}

	public void setLog(Logger log)
	{
		Logger.setLogger(log);
	}

	public void start()
	{
		try
		{
			Thread clientListener = null;
			long lastTime = System.currentTimeMillis();
			while(true)
			{
				if(clientListener == null || !clientListener.isAlive())
				{
					clientListener = new Thread(new ClientListener(context));
					clientListener.setDaemon(true);
					clientListener.start();
				}
				// 关闭空闲的客户端
				if(System.currentTimeMillis() > lastTime + 50000)
				{
					context.closeIdleClient();
					lastTime = System.currentTimeMillis();
				}
				try
				{
					Thread.sleep(10000);
				}
				catch(InterruptedException e)
				{
				}
			}
		}
		catch(Exception e)
		{
			e.getStackTrace();
		}
	}

	public static void main(String[] args) throws Exception
	{
		String filename = args.length > 0 ? args[0] : "classpath:server.json";
		String json = FileUtil.readTextFile(filename);
		Config config = GsonUtil.toBean(json, Config.class);
		Server server = new Server();
		server.setPort(config.port);
		server.setTimeout(config.timeout);
		server.setToken(config.token);
		server.setLog(new LoggerImpl().setEnableLog(config.enableLog));
		server.start();
	}
}
