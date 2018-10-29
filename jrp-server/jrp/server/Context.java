package jrp.server;

import java.io.IOException;
import java.util.*;
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

	private Map<String, Queue<OuterLink>> outerLinkQueueMap = new ConcurrentHashMap<>();
	private Map<Integer, TunnelInfo> tunnelInfoMap = new ConcurrentHashMap<>();

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
		return tunnelInfoMap.get(remotePort);
	}

	public void putTunnelInfo(int remotePort, TunnelInfo tunnelInfo)
	{
		tunnelInfoMap.put(remotePort, tunnelInfo);
	}

	public List<TunnelInfo> getTunnelInfos(String clientId)
	{
		List<TunnelInfo> list = new ArrayList<>();
		for(Map.Entry<Integer, TunnelInfo> entry : tunnelInfoMap.entrySet())
		{
			if(entry.getValue().getClientId().equals(clientId))
			{
				list.add(entry.getValue());
			}
		}
		return list;
	}

	public void delTunnelInfo(int remotePort)
	{
		TunnelInfo tunnel = tunnelInfoMap.get(remotePort);
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
		tunnelInfoMap.remove(remotePort);
	}

	public void delTunnelInfos(String clientId)
	{
		Iterator<Map.Entry<Integer, TunnelInfo>> it = tunnelInfoMap.entrySet().iterator();
		while(it.hasNext())
		{
			TunnelInfo tunnel = it.next().getValue();
			if(tunnel.getClientId().equals(clientId))
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
