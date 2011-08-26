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
		Integer property = InputOutput.jails.getInt(jail.getName() + "." + setting, (Integer) null);
		if (property == null)
			property = InputOutput.global.getInt(setting.getString(), (Integer) null);
		return property;
	}
	
	public Double getDouble(Setting setting)
	{
		Double property = InputOutput.jails.getDouble(jail.getName() + "." + setting, (Double) null);
		if (property == null)
			property = InputOutput.global.getDouble(setting.getString(), (Double) null);
		return property;
	}
	
	public String getString(Setting setting)
	{
		String property = InputOutput.jails.getString(jail.getName() + "." + setting, (String) null);
		if (property == null)
			property = InputOutput.global.getString(setting.getString(), (String) null);
		return property;
	}
	
	public Boolean getBoolean(Setting setting)
	{
		Boolean property = InputOutput.jails.getBoolean(jail.getName() + "." + setting, (Boolean) null);
		if (property == null)
			property = InputOutput.global.getBoolean(setting.getString(), (Boolean) null);
		return property;
	}
	
	public List<Object> getList(Setting setting)
	{
		List<Object> property = InputOutput.jails.getList(jail.getName() + "." + setting);
		if (property == null)
			property = InputOutput.global.getList(setting.getString());
		return property;
	}
}
