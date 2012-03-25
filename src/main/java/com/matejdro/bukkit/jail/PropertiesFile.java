package com.matejdro.bukkit.jail;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Level;

public class PropertiesFile {
private HashMap<String, PropertiesEntry> map;
private File file;
private boolean modified;

public PropertiesFile(File file) {
this.file = file;
map = new HashMap<String, PropertiesEntry>();
Scanner scan;
try {
if (!file.exists())
file.createNewFile();
scan = new Scanner(file);
while (scan.hasNextLine()) {
String line = scan.nextLine();
if (!line.contains("="))
continue;
int equals = line.indexOf("=");
int commentIndex = line.length();
if (line.contains("#")) {
commentIndex = line.indexOf("#");
}

String key = line.substring(0, equals).trim();
if (key.equals(""))
continue;
String value = line.substring(equals + 1, commentIndex).trim();
map.put(key, new PropertiesEntry(value));
}
} catch (FileNotFoundException e) {
Jail.log.log(Level.SEVERE, "[Jail]: Cannot read file " + file.getName());
} catch (IOException e) {
Jail.log.log(Level.SEVERE, "[Jail]: Cannot create file " + file.getName());
}
}

public boolean getBoolean(String key, Boolean defaultValue) {
if (map.containsKey(key)) {
return Boolean.parseBoolean(map.get(key).value);
} else {
map.put(key, new PropertiesEntry(defaultValue.toString()));
modified = true;
return defaultValue;
}
}

public String getString(String key, String defaultValue) {
if (map.containsKey(key)) {
return map.get(key).value;
} else {
map.put(key, new PropertiesEntry(defaultValue.toString()));
modified = true;
return defaultValue;
}
}

public int getInt(String key, Integer defaultValue) {
if (map.containsKey(key)) {
try {
return Integer.parseInt(map.get(key).value);
} catch (Exception e) {
Jail.log.log(Level.WARNING, "[Jail]: Trying to get Integer from " + key + ": " + map.get(key).value);
return 0;
}
} else {
map.put(key, new PropertiesEntry(defaultValue.toString()));
modified = true;
return defaultValue;
}
}

public double getDouble(String key, Double defaultValue) {
if (map.containsKey(key)) {
try {
return Double.parseDouble(map.get(key).value);
} catch (Exception e) {
Jail.log.log(Level.WARNING, "[Jail]: Trying to get Double from " + key + ": " + map.get(key).value);
return 0;
}
} else {
map.put(key, new PropertiesEntry(defaultValue.toString()));
modified = true;
return defaultValue;
}
}

public void save() {
if(!modified) return;
BufferedWriter bwriter = null;
FileWriter fwriter = null;
try {
if (!file.exists())
file.createNewFile();
fwriter = new FileWriter(file);
bwriter = new BufferedWriter(fwriter);
for (Entry<String, PropertiesEntry> entry : map.entrySet()) {
StringBuilder builder = new StringBuilder();
builder.append(entry.getKey());
builder.append(" = ");
builder.append(entry.getValue().value);
bwriter.write(builder.toString());
bwriter.newLine();
}
bwriter.flush();
} catch (IOException e) {
Jail.log.log(Level.SEVERE, "[Jail]: IO Exception with file " + file.getName());
} finally {
try {
if (bwriter != null) {
bwriter.flush();
bwriter.close();
} if (fwriter != null) {
fwriter.close();
}
} catch (IOException e) {
Jail.log.log(Level.SEVERE, "[Jail]: IO Exception with file " + file.getName() + " (on close)");
}
}

}

private class PropertiesEntry {
public String value;

public PropertiesEntry(String value) {
this.value = value;
}
}
}
