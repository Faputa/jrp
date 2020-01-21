/**
 * 处理Ngrok建立的连接
 */
package jrp.server.handler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import jrp.Protocol;
import jrp.log.Logger;
import jrp.server.Context;
import jrp.server.Message;
import jrp.server.listener.TcpListener;
import jrp.server.model.ClientInfo;
import jrp.server.model.Request;
import jrp.socket.PacketReader;
import jrp.socket.SocketHelper;
import jrp.util.GsonUtil;

public class ClientHandler implements Runnable
{
	private Socket socket;
	private Context context;
	private Logger log = Logger.getLogger();

	public ClientHandler(Socket socket, Context context)
	{
		this.socket = socket;
		this.context = context;
	}

	@Override
	public void run()
	{
		String clientId = null;
		try (Socket socket = this.socket)
		{
			PacketReader pr = new PacketReader(socket, context.timeout);
			String msg = pr.read();
			if(msg == null)
			{
				return;
			}
			log.info("收到客户端信息：" + msg);
			Protocol protocol = GsonUtil.toBean(msg, Protocol.class);
			if("RegProxy".equals(protocol.Type))
			{
				ClientInfo client = context.getClientInfo(protocol.ClientId);
				if(client == null)
				{
					// 客户端信息不存在
					return;
				}
				Request request = client.getRequestQueue().poll(60, TimeUnit.SECONDS);
				if(request == null)
				{
					// 现有逻辑基本上不可能拿不到外部请求
					return;
				}
				try
				{
					SocketHelper.sendpack(socket, Message.StartProxy(request.getRemotePort()));
					request.setProxySocket(socket);
				}
				catch(Exception e)
				{
					log.err(e.toString());
				}
				try (Socket outerSocket = request.getOuterSocket())
				{
					SocketHelper.forward(socket, outerSocket);
				}
				catch(Exception e)
				{
					// ignore
				}
				return;
			}
			if("Auth".equals(protocol.Type))
			{
				if(context.token != null && !context.token.equals(protocol.AuthToken))
				{
					SocketHelper.sendpack(socket, Message.AuthResp(null, "authtoken校验失败"));
					return;
				}
				clientId = "X" + String.valueOf(System.currentTimeMillis());
				context.createClientInfo(clientId, socket);
				SocketHelper.sendpack(socket, Message.AuthResp(clientId, null));
				while(true)
				{
					msg = pr.read();
					if(msg == null)
					{
						break;
					}
					log.info("收到客户端信息：" + msg);
					protocol = GsonUtil.toBean(msg, Protocol.class);
					if("ReqTunnel".equals(protocol.Type))
					{
						int remotePort = protocol.RemotePort;
						if(context.getTunnelInfo(remotePort) != null)
						{
							context.getTunnelInfo(remotePort).close();
						}
						ServerSocket serverSocket;
						try
						{
							serverSocket = SocketHelper.newServerSocket(remotePort);
						}
						catch(Exception e)
						{
							String error = "端口 " + remotePort + " 已经被占用";
							SocketHelper.sendpack(socket, Message.NewTunnel(null, error));
							break;
						}
						Thread thread = new Thread(new TcpListener(serverSocket, context));
						thread.setDaemon(true);
						thread.start();
						context.createTunnelInfo(remotePort, clientId, serverSocket);
						SocketHelper.sendpack(socket, Message.NewTunnel(remotePort, null));
					}
					else if("Ping".equals(protocol.Type))
					{
						SocketHelper.sendpack(socket, Message.Pong());
					}
				}
			}
		}
		catch(Exception e)
		{
			log.err(e.toString());
		}
		if(clientId != null)
		{
			context.deleteClientInfo(clientId);
			context.deleteTunnelInfo(clientId);
		}
	}
}
