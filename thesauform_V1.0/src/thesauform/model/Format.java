package thesauform.model;

public class Format {
	public Format(){
		
	}
	
	public static String formatName(String name){
		name = name.trim();
		name = name.toLowerCase();
		name = name.replace(" ", "_");
		name = name.replace(":", "_");
		name = name.replaceAll("[()/]", "");
		name = name.replace("<", "");name = name.replace(">", "");
		name = name.replace("_&_", " ");
		name = name.replace("&", " ");
		
		char[] char_table = name.toCharArray();
		char_table[0]=Character.toUpperCase(char_table[0]);
		name = new String(char_table);		
		
		return name;
	}
	
	public static String formatUnit(String unit){
		unit = unit.trim();
		unit = unit.replace(" ", "");
		
		return unit;
	}
	
	public static String formatDef(String def){
		def = def.trim();
		def = def.replace(" ", "_");
		
		return def;
	}
	
	public static String printDef(String def){
		def = def.trim();
		def = def.replace("_", " ");
		
		return def;
	}
}
