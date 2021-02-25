package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "main_page_folders")
@javax.persistence.SequenceGenerator(name="seq_main_page_folders_id",sequenceName="seq_main_page_folders_id", allocationSize = 1)
public class MainPageFolders {
	
	public MainPageFolders() {
		this.name_en = "";
		this.name_ru = "";
		this.name_tr = "";
		this.mpf_parent_id = 0L;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_main_page_folders_id")
    private Long mpf_id;
	
	@Column(name = "mpf_parent_id")
	private Long mpf_parent_id;
	
	@Column(name = "name_ru")
	private String name_ru;
	
	@Column(name = "name_en")
	private String name_en;
	
	@Column(name = "name_tr")
	private String name_tr;

	public Long getMpf_parent_id() {
		return mpf_parent_id;
	}

	public void setMpf_parent_id(Long mpf_parent_id) {
		this.mpf_parent_id = mpf_parent_id;
	}

	public Long getMpf_id() {
		return mpf_id;
	}

	public String getName_ru() {
		return name_ru;
	}

	public void setName_ru(String name_ru) {
		this.name_ru = name_ru;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getName_tr() {
		return name_tr;
	}

	public void setName_tr(String name_tr) {
		this.name_tr = name_tr;
	}
	
	public String getName(String lang){
		if(lang.equals("en") && this.name_en != null && this.name_en.length() > 0){
			return this.name_en;
		} else if(lang.equals("tr") && this.name_tr != null && this.name_tr.length() > 0){
			return this.name_tr;
		}
		
		return this.name_ru;
	}
}
