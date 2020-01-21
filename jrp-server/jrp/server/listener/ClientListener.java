/**
 * 监听Ngrok的连接请求
 */
package jrp.server.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import jrp.log.Logger;
import jrp.server.Context;
import jrp.server.handler.ClientHandler;
import jrp.socket.SocketHelper;

public class ClientListener implements Runnable
{
	private Context context;
	private Logger log = Logger.getLogger();

	public ClientListener(Context context)
	{
		this.context = context;
	}

	@Override
	public void run()
	{
		try (ServerSocket ssocket = SocketHelper.newServerSocket(context.port))
		{
			log.info("监听建立成功：[%s]", context.port);
			while(true)
			{
				Socket socket = ssocket.accept();
				Thread thread = new Thread(new ClientHandler(socket, context));
				thread.setDaemon(true);
				thread.start();
			}
		}
		catch(IOException e)
		{
			log.err(e.toString());
		}
	}
}
