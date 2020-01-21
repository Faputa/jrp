package jrp.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jrp.server.model.ClientInfo;
import jrp.server.model.TunnelInfo;

public class Context
{
	public int port;
	public int timeout;
	public String token;
	// client info
	private Map<String, ClientInfo> clientInfoMap = new ConcurrentHashMap<>();
	// tunnel info
	private Map<Integer, TunnelInfo> tunnelInfoMap = new ConcurrentHashMap<>();

	public ClientInfo getClientInfo(String clientId)
	{
		return clientInfoMap.get(clientId);
	}

	public void createClientInfo(String clientId, Socket controlSocket)
	{
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setControlSocket(controlSocket);
		clientInfoMap.put(clientId, clientInfo);
	}

	public void deleteClientInfo(String clientId)
	{
		ClientInfo clientInfo = clientInfoMap.get(clientId);
		if(clientInfo != null)
		{
			clientInfo.close();
			clientInfoMap.remove(clientId);
		}
	}

	public TunnelInfo getTunnelInfo(int remotePort)
	{
		return tunnelInfoMap.get(remotePort);
	}

	public void createTunnelInfo(int remotePort, String clientId, ServerSocket tcpServerSocket)
	{
		TunnelInfo tunnelInfo = new TunnelInfo();
		tunnelInfo.setClientId(clientId);
		tunnelInfo.setTcpServerSocket(tcpServerSocket);
		tunnelInfoMap.put(remotePort, tunnelInfo);
	}

	public void deleteTunnelInfo(String clientId)
	{
		Iterator<Entry<Integer, TunnelInfo>> it = tunnelInfoMap.entrySet().iterator();
		while(it.hasNext())
		{
			TunnelInfo ti = it.next().getValue();
			if(clientId.equals(ti.getClientId()))
			{
				ti.close();
				it.remove();
			}
		}
	}

	/**
	 * 关闭空闲的客户端
	 */
	public void closeIdleClient()
	{
		Set<String> clientIdSet = new HashSet<>();
		for(TunnelInfo ti : tunnelInfoMap.values())
		{
			clientIdSet.add(ti.getClientId());
		}
		for(Map.Entry<String, ClientInfo> e : clientInfoMap.entrySet())
		{
			if(!clientIdSet.contains(e.getKey()))
			{
				e.getValue().close();
			}
		}
	}
}
