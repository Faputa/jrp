/**
 * 与Ngrokd建立控制连接，并交换控制信息
 */
package jrp.client.connect;

import java.io.IOException;
import java.net.Socket;

import jrp.Protocol;
import jrp.client.Context;
import jrp.client.Message;
import jrp.client.model.Tunnel;
import jrp.log.Logger;
import jrp.socket.PacketReader;
import jrp.socket.SocketHelper;
import jrp.util.GsonUtil;

public class ControlConnect implements Runnable
{
	private Socket socket;
	private Context context;
	private Logger log = Logger.getLogger();

	public ControlConnect(Socket socket, Context context)
	{
		this.socket = socket;
		this.context = context;
	}

	@Override
	public void run()
	{
		try (Socket socket = this.socket)
		{
			SocketHelper.sendpack(socket, Message.Auth(context.authToken));
			PacketReader pr = new PacketReader(socket);
			String msg = pr.read();
			if(msg == null)
			{
				// 服务器主动关闭连接，正常退出
				return;
			}
			log.info("收到服务器信息：" + msg);
			Protocol protocol = GsonUtil.toBean(msg, Protocol.class);
			if("AuthResp".equals(protocol.Type))
			{
				if(protocol.Error != null)
				{
					log.err("客户端认证失败：" + protocol.Error);
					return;
				}
				String clientId = protocol.ClientId;
				log.info("客户端注册成功：" + clientId);
				for(Tunnel tunnel : context.tunnelList)
				{
					SocketHelper.sendpack(socket, Message.ReqTunnel(tunnel));
				}
				while(true)
				{
					msg = pr.read();
					if(msg == null)
					{
						return;
					}
					log.info("收到服务器信息：" + msg);
					protocol = GsonUtil.toBean(msg, Protocol.class);
					if("ReqProxy".equals(protocol.Type))
					{
						try
						{
							Socket remoteSocket = SocketHelper.newSocket(context.serverHost, context.serverPort);
							Thread thread = new Thread(new ProxyConnect(remoteSocket, clientId, context));
							thread.setDaemon(true);
							thread.start();
						}
						catch(Exception e)
						{
							log.err(e.toString());
						}
					}
					else if("NewTunnel".equals(protocol.Type))
					{
						if(protocol.Error != null)
						{
							log.err("管道注册失败：" + protocol.Error);
							return;
						}
						log.info("管道注册成功：%s:%d", context.serverHost, protocol.RemotePort);
					}
				}
			}
		}
		catch(IOException e)
		{
			log.err(e.toString());
		}
	}
}
