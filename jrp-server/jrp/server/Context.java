package jrp.server;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import jrp.log.Logger;
import jrp.log.LoggerImpl;
import jrp.server.model.OuterLink;
import jrp.server.model.TunnelInfo;

public class Context
{
	public int port;
	public int timeout;
	public String token;
	public Logger log = new LoggerImpl();// 如果没有注入日志，则使用默认日志

	// client info
	private Map<String, Queue<OuterLink>> outerLinkQueueMap = new ConcurrentHashMap<>();
	private Map<String, Socket> controlSocketMap = new ConcurrentHashMap<>();
	// tunnel info
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

	public void initClientInfo(String clientId, Socket controlSocket)
	{
		outerLinkQueueMap.put(clientId, new ConcurrentLinkedQueue<OuterLink>());
		controlSocketMap.put(clientId, controlSocket);
	}

	public TunnelInfo getTunnelInfo(int remotePort)
	{
		return tunnelInfoMap.get(remotePort);
	}

	public void putTunnelInfo(int remotePort, TunnelInfo tunnelInfo)
	{
		tunnelInfoMap.put(remotePort, tunnelInfo);
	}

	public void delClientInfo(String clientId)
	{
		outerLinkQueueMap.remove(clientId);
		controlSocketMap.remove(clientId);
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

	public void closeIdleClient()
	{
		Set<String> flagSet = new HashSet<>();
		for(TunnelInfo tunnel : tunnelInfoMap.values())
		{
			flagSet.add(tunnel.getClientId());
		}
		for(Map.Entry<String, Socket> entry : controlSocketMap.entrySet())
		{
			if(!flagSet.contains(entry.getKey()))
			{
				try
				{
					entry.getValue().close();
				}
				catch(IOException e)
				{
				}
			}
		}
	}
}
