package general.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "addr_type")
public class AddressType implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1125479792855119583L;

	@Id
	private Long id;

	@Column(name = "name_en")
	private String nameEn;

	@Column(name = "name_ru")
    private String nameRu;

	@Column(name = "name_tr")
    private String nameTr;
	
	@Column(name = "info")
    private String info;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameRu() {
		return nameRu;
	}

	public void setNameRu(String nameRu) {
		this.nameRu = nameRu;
	}

	public String getNameTr() {
		return nameTr;
	}

	public void setNameTr(String nameTr) {
		this.nameTr = nameTr;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getName(String a_lang){
		if (a_lang.equals("en")) return this.nameEn;
		else if (a_lang.equals("tr")) return this.nameTr;
		else return this.nameRu;
		
	}


}
