/**
 * 处理用户建立的tcp连接
 */
package jrp.server.handler;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import jrp.log.Logger;
import jrp.server.Context;
import jrp.server.Message;
import jrp.server.model.ClientInfo;
import jrp.server.model.Request;
import jrp.server.model.TunnelInfo;
import jrp.socket.SocketHelper;

public class TcpHandler implements Runnable
{
	private Socket socket;
	private Context context;
	private Logger log = Logger.getLogger();

	public TcpHandler(Socket socket, Context context)
	{
		this.socket = socket;
		this.context = context;
	}

	@Override
	public void run()
	{
		log.info("收到外部请求");
		try (Socket socket = this.socket)
		{
			int remotePort = socket.getLocalPort();
			TunnelInfo tunnel = context.getTunnelInfo(remotePort);
			if(tunnel == null)
			{
				return;
			}
			ClientInfo client = context.getClientInfo(tunnel.getClientId());
			Request request = new Request();
			request.setRemotePort(remotePort);
			request.setOuterSocket(socket);
			try
			{
				SocketHelper.sendpack(client.getControlSocket(), Message.ReqProxy());
			}
			catch(IOException e)
			{
				return;
			}
			client.getRequestQueue().put(request);
			try (Socket proxySocket = request.getProxySocket(60, TimeUnit.SECONDS))
			{// 最多等待60秒
				SocketHelper.forward(socket, proxySocket);
			}
			catch(Exception e)
			{
				// ignore
			}
		}
		catch(Exception e)
		{
			log.err(e.toString());
		}
	}
}
