package com.matejdro.bukkit.jail;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class Settings {
	private JailZone jail;
	
	public Settings(JailZone zone)
	{
		jail = zone;
	}
	
	public Integer getInt(Setting setting)
	{
		Object property;
		property = InputOutput.jails.get(jail.getName() + "." + setting.getString());
		if (property == null)
			property = getGlobalProperty(setting);
		return (Integer) property;
	}
	
	public Double getDouble(Setting setting)
	{
		Object property;
		property = InputOutput.jails.get(jail.getName() + "." + setting.getString());
		if (property == null)
			property = getGlobalProperty(setting);
		if (!(property instanceof Double)) property = Double.parseDouble(property.toString());
		return (Double) property;
	}
	
	public String getString(Setting setting)
	{
		Object property;
		property = InputOutput.jails.get(jail.getName() + "." + setting.getString());
		if (property == null)
			property = getGlobalProperty(setting);
		return (String) property;

	}
	
	public Boolean getBoolean(Setting setting)
	{
		Object property;
		property = InputOutput.jails.get(jail.getName() + "." + setting.getString());
		if (property == null)
			property = getGlobalProperty(setting);
		return (Boolean) property;

	}
	
	public List<?> getList(Setting setting)
	{
		Object property;
		property = InputOutput.jails.get(jail.getName() + "." + setting.getString());
		if (property == null)
			property = getGlobalProperty(setting);
		
		return (List<?>) property;

	}
	
	public void setProperty(Setting setting, Object object)
	{
		InputOutput.jails.get(jail.getName() + "." + setting.getString(), object);
		try {
			InputOutput.jails.save(new File("plugins" + File.separator + "Jail","jails.yml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Object getGlobalProperty(Setting setting)
	{
		Object property = InputOutput.global.get(setting.getString());
		if (property == null)
		{
			Jail.log.warning("[Jail] Configuration entry missing: " + setting.getString());
			property = setting.getDefault();
		}
		
		return property;
	}

	public static Boolean getGlobalBoolean(Setting setting)
	{
		return 	(Boolean) getGlobalProperty(setting);
	}
	
	public static Integer getGlobalInt(Setting setting)
	{
		return 	(Integer) getGlobalProperty(setting);
	}

	public static String getGlobalString(Setting setting)
	{
		return 	(String) getGlobalProperty(setting);
	}
	
	public static List<?> getGlobalList(Setting setting)
	{
		return 	(List<?>) getGlobalProperty(setting);
	}
}
