package jrp.util;

public class GsonUtil
{
	private GsonUtil()
	{
	}
	private static com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	public static String toJson(Object object)
	{
		return gson.toJson(object);
	}

	public static <T> T toBean(String json, Class<T> classOfT)
	{
		return gson.fromJson(json, classOfT);
	}
}
