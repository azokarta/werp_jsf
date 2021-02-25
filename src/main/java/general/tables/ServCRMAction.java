package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Serv_CRMAction")
public class ServCRMAction {
	
	@Id
	private Long id;
	
	@Column(name = "name_en")
    private String nameEN;
	
	@Column(name = "name_ru")
    private String nameRU;
	
	@Column(name = "name_TR")
    private String nameEn;
	
	@Column(name = "info")
    private String info;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNameEN() {
		return nameEN;
	}

	public void setNameEN(String nameEN) {
		this.nameEN = nameEN;
	}

	public String getNameRU() {
		return nameRU;
	}

	public void setNameRU(String nameRU) {
		this.nameRU = nameRU;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
