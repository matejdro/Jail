package com.matejdro.bukkit.jail;

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
		property = InputOutput.jails.getProperty(jail.getName() + "." + setting.getString());
		if (property == null)
			property = InputOutput.global.getProperty(setting.getString());
		return (Integer) property;
	}
	
	public Double getDouble(Setting setting)
	{
		Object property;
		property = InputOutput.jails.getProperty(jail.getName() + "." + setting.getString());
		if (property == null)
			property = InputOutput.global.getProperty(setting.getString());
		return (Double) property;
	}
	
	public String getString(Setting setting)
	{
		Object property;
		property = InputOutput.jails.getProperty(jail.getName() + "." + setting.getString());
		if (property == null)
			property = InputOutput.global.getProperty(setting.getString());
		return (String) property;

	}
	
	public Boolean getBoolean(Setting setting)
	{
		Object property;
		property = InputOutput.jails.getProperty(jail.getName() + "." + setting.getString());
		if (property == null)
			property = InputOutput.global.getProperty(setting.getString());
		return (Boolean) property;

	}
	
	public List<?> getList(Setting setting)
	{
		Object property;
		property = InputOutput.jails.getProperty(jail.getName() + "." + setting.getString());
		if (property == null)
			property = InputOutput.global.getProperty(setting.getString());
		return (List<?>) property;

	}
	
	public void setProperty(Setting setting, Object object)
	{
		InputOutput.jails.setProperty(jail.getName() + "." + setting.getString(), object);
		InputOutput.jails.save();
	}
}
