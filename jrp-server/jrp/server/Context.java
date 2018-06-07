package jrp.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import jrp.log.Logger;
import jrp.log.LoggerImpl;
import jrp.server.model.OuterLink;
import jrp.server.model.TunnelInfo;

public class Context
{
	private int port;
	private String token;
	private Logger log = new LoggerImpl();// 如果没有注入日志，则使用默认日志

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public Logger getLog()
	{
		return log;
	}

	public void setLog(Logger log)
	{
		this.log = log;
	}

	private Map<String, Queue<OuterLink>> outerLinkQueueMap = new ConcurrentHashMap<String, Queue<OuterLink>>();
	private Map<String, TunnelInfo> tunnelInfoMap = new ConcurrentHashMap<String, TunnelInfo>();

	public OuterLink pollOuterLink(String clientId)
	{
		Queue<OuterLink> queue = outerLinkQueueMap.get(clientId);
		if(queue == null)
		{
			return null;
		}
		return queue.poll();
	}

	public void offerOuterLink(String clientId, OuterLink link)
	{
		outerLinkQueueMap.get(clientId).offer(link);
	}

	public void initOuterLinkQueue(String clientId)
	{
		outerLinkQueueMap.put(clientId, new ConcurrentLinkedQueue<OuterLink>());
	}

	public void delOuterLinkQueue(String clientId)
	{
		outerLinkQueueMap.remove(clientId);
	}

	public TunnelInfo getTunnelInfo(int remotePort)
	{
		return tunnelInfoMap.get(String.valueOf(remotePort));
	}

	public void putTunnelInfo(int remotePort, TunnelInfo tunnelInfo)
	{
		tunnelInfoMap.put(String.valueOf(remotePort), tunnelInfo);
	}

	public void delTunnelInfo(String clientId)
	{
		Iterator<Map.Entry<String, TunnelInfo>> it = tunnelInfoMap.entrySet().iterator();
		while(it.hasNext())
		{
			TunnelInfo tunnel = it.next().getValue();
			if(clientId.equals(tunnel.getClientId()))
			{
				if(tunnel.getTcpServerSocket() != null)
				{
					try
					{
						tunnel.getTcpServerSocket().close();
					}
					catch(IOException e)
					{
					}
				}
				it.remove();
			}
		}
	}
}
