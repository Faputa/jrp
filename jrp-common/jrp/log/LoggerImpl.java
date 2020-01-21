package jrp.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerImpl extends Logger
{
	public static String getTime()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	private boolean enableLog = true;

	public LoggerImpl setEnableLog(boolean enableLog)
	{
		this.enableLog = enableLog;
		return this;
	}

	@Override
	public synchronized void info(String fmt, Object... args)
	{
		if(enableLog)
		{
			System.out.printf("[%s] %s\n", getTime(), String.format(fmt, args));
		}
	}

	@Override
	public synchronized void err(String fmt, Object... args)
	{
		if(enableLog)
		{
			System.err.printf("[%s] %s\n", getTime(), String.format(fmt, args));
		}
	}
}
