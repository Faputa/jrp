/**
 * 处理Ngrok建立的连接
 */
package jrp.server.server;

import java.net.ServerSocket;
import java.net.Socket;

import jrp.Protocol;
import jrp.log.Logger;
import jrp.server.Context;
import jrp.server.Message;
import jrp.server.listener.TcpListener;
import jrp.server.model.OuterLink;
import jrp.server.model.TunnelInfo;
import jrp.socket.PacketReader;
import jrp.socket.SocketHelper;
import jrp.util.GsonUtil;
import jrp.util.Util;

public class ClientServer implements Runnable
{
	private Socket socket;
	private Context context;
	private Logger log;

	public ClientServer(Socket socket, Context context)
	{
		this.socket = socket;
		this.context = context;
		this.log = context.log;
	}

	@Override
	public void run()
	{
		String clientId = null;
		try(Socket socket = this.socket)
		{
			PacketReader pr = new PacketReader(socket, context.timeout);
			while(true)
			{
				String msg = pr.read();
				if(msg == null)
				{
					break;
				}
				log.log("收到客户端信息：" + msg);
				Protocol protocol = GsonUtil.toBean(msg, Protocol.class);
				if("Auth".equals(protocol.Type))
				{
					if(context.token != null && !context.token.equals(protocol.AuthToken))
					{
						SocketHelper.sendpack(socket, Message.AuthResp(null, "authtoken校验失败"));
						return;
					}
					clientId = Util.MD5(String.valueOf(System.currentTimeMillis()));
					context.initClientInfo(clientId, socket);
					SocketHelper.sendpack(socket, Message.AuthResp(clientId, null));
				}
				else if("RegProxy".equals(protocol.Type))
				{
					OuterLink link  = context.pollOuterLink(protocol.ClientId);
					if(link == null)
					{
						break;
					}
					SocketHelper.sendpack(socket, Message.StartProxy(link.getRemotePort()));
					try(Socket outerSocket = link.getOuterSocket())
					{
						link.putProxySocket(socket);
						SocketHelper.forward(socket, outerSocket);
					}
					catch(Exception e)
					{
					}
					break;
				}
				else if("ReqTunnel".equals(protocol.Type))
				{
					int remotePort = protocol.RemotePort;
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

					TunnelInfo tunnel = new TunnelInfo();
					tunnel.setClientId(clientId);
					tunnel.setControlSocket(socket);
					tunnel.setTcpServerSocket(serverSocket);
					context.putTunnelInfo(remotePort, tunnel);
					SocketHelper.sendpack(socket, Message.NewTunnel(remotePort, null));
				}
				else if("Ping".equals(protocol.Type))
				{
					SocketHelper.sendpack(socket, Message.Pong());
				}
			}
		}
		catch(Exception e)
		{
			log.err(e.toString());
		}
		if(clientId != null)
		{
			context.delClientInfo(clientId);
		}
	}
}
