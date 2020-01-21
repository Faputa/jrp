package jrp.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import jrp.util.ByteUtil;

public class SocketHelper
{
	private SocketHelper()
	{
	}

	public static Socket newSocket(String host, int port) throws IOException
	{
		return new Socket(host, port);
	}

	public static ServerSocket newServerSocket(int port) throws IOException
	{
		return new ServerSocket(port);
	}

	public static void sendpack(Socket socket, String msg) throws IOException
	{
		OutputStream os = socket.getOutputStream();
		byte[] bs = msg.getBytes();
		os.write(ByteUtil.concat(ByteUtil.encodeInt(bs.length), bs));
		os.flush();
	}

	public static void sendbuf(Socket socket, byte[] buf) throws IOException
	{
		OutputStream os = socket.getOutputStream();
		os.write(buf);
		os.flush();
	}

	public static byte[] recvbuf(Socket socket) throws IOException
	{
		InputStream is = socket.getInputStream();
		int len;
		byte[] buf = new byte[1024];
		if((len = is.read(buf)) == -1)
		{
			return null;
		}
		return ByteUtil.subArr(buf, 0, len);
	}

	public static void safeClose(Socket socket)
	{
		if(socket == null)
		{
			return;
		}
		try
		{
			socket.close();
		}
		catch(IOException e)
		{
			// ignore
		}
	}

	public static void safeClose(ServerSocket serverSocket)
	{
		if(serverSocket == null)
		{
			return;
		}
		try
		{
			serverSocket.close();
		}
		catch(IOException e)
		{
			// ignore
		}
	}

	public static void forward(Socket s1, Socket s2) throws IOException
	{
		InputStream is = s1.getInputStream();
		OutputStream os = s2.getOutputStream();
		int len;
		byte[] buf = new byte[1024];
		while((len = is.read(buf)) != -1)
		{
			os.write(buf, 0, len);
		}
	}
}
