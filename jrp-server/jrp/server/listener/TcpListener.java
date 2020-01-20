/**
 * 监听用户的tcp请求
 */
package jrp.server.listener;

import java.net.ServerSocket;
import java.net.Socket;

import jrp.server.Context;
import jrp.server.handler.TcpHandler;

public class TcpListener implements Runnable
{
	private ServerSocket ssocket;
	private Context context;

	public TcpListener(ServerSocket ssocket, Context context)
	{
		this.ssocket = ssocket;
		this.context = context;
	}

	@Override
	public void run()
	{
		try(ServerSocket ssocket = this.ssocket)
		{
			while(true)
			{
				Socket socket = ssocket.accept();
				Thread thread = new Thread(new TcpHandler(socket, context));
				thread.setDaemon(true);
				thread.start();
			}
		}
		catch(Exception e)
		{
		}
	}
}
