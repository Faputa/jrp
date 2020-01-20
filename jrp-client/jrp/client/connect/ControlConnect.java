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
	private Logger log;

	public ControlConnect(Socket socket, Context context)
	{
		this.socket = socket;
		this.context = context;
		this.log = context.getLog();
	}

	@Override
	public void run()
	{
		try(Socket socket = this.socket)
		{
			String clientId = null;
			SocketHelper.sendpack(socket, Message.Auth(context.getAuthToken()));
			PacketReader pr = new PacketReader(socket);
			while(true)
			{
				String msg = pr.read();
				if(msg == null)
				{
					return;
				}
				log.log("收到服务器信息：" + msg);
				Protocol protocol = GsonUtil.toBean(msg, Protocol.class);
				if("ReqProxy".equals(protocol.Type))
				{
					try
					{
						Socket remoteSocket = SocketHelper.newSSLSocket(context.getServerHost(), context.getServerPort());
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
					if(protocol.Error == null || "".equals(protocol.Error))
					{
						log.log("管道注册成功：%s:%d", context.getServerHost(), protocol.RemotePort);
					}
					else
					{
						log.err("管道注册失败：" + protocol.Error);
						try{Thread.sleep(30);}catch(InterruptedException e){}
					}
				}
				else if("AuthResp".equals(protocol.Type))
				{
					if(protocol.Error == null || "".equals(protocol.Error))
					{
						clientId = protocol.ClientId;
						log.log("客户端注册成功：" + clientId);
						SocketHelper.sendpack(socket, Message.Ping());
						for(Tunnel tunnel : context.getTunnelList())
						{
							SocketHelper.sendpack(socket, Message.ReqTunnel(tunnel));
						}
					}
					else
					{
						log.err("客户端认证失败：" + protocol.Error);
						return;
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
