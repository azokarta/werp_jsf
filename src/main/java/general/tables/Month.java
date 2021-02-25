package general.tables;

public class Month {
	public Month(int a_id, String a_name, String a_language)
	{
		this.id = a_id;
		this.name = a_name;
		this.language = a_language;					
	}
	private int id;
	private String name = "";
	private String language = "";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
}
