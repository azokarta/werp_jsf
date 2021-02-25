package general.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "contract_status")
public class ContractStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8405266076152139402L;

	@Id 
	private Long contract_status_id;
		
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "spras")
	private String spras;
	
	@Column(name = "ru")
	private String ru;
	
	@Column(name = "en")
	private String en;
	
	@Column(name = "tr")
	private String tr;
	
	@Column(name = "ACCESS_LEVEL")
	private int accessLevel;
	
	@Column(name = "ISACTIVE")
	private int isActive;
	
	public static final int STATUS_STANDARD = 1;
	public static final int STATUS_GIFT = 2;
	public static final int STATUS_CANCELLED = 3;
	public static final int STATUS_PROBLEM_REAL = 4;
	public static final int STATUS_CLOSED = 5;
	//public static final int STATUS_REISSUED = 6;
	public static final int STATUS_FORCANCEL = 7;
	public static final int STATUS_COURT = 8;
	public static final int STATUS_PRECOURT = 9;
	public static final int STATUS_COUNTRYSIDE = 10;
	public static final int STATUS_LAWYER = 11;
	public static final int STATUS_TEMP_CHARGED = 12;
	public static final int STATUS_TEMP_RETURNED = 13;
	public static final int STATUS_PROBLEM_6MONTHS = 14;
	public static final int STATUS_PROBLEM_DEALER = 15;
	public static final int STATUS_RESOLD = 17;
	public static final int STATUS_FORSERVICE = 18;
	
	/*
	1	1	1000	Стандарт	ru
	2	2	1000	Подарок	ru
	3	3	1000	Отменен	ru
	4	4	1000	Проблемный	ru
	5	5	1000	Выполнен	ru
	6	6	1000	Переоформлен	ru
	*/

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public String getRu() {
		return ru;
	}

	public void setRu(String ru) {
		this.ru = ru;
	}

	public String getEn() {
		return en;
	}

	public void setEn(String en) {
		this.en = en;
	}

	public String getTr() {
		return tr;
	}

	public void setTr(String tr) {
		this.tr = tr;
	}

	public int getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	public Long getContract_status_id() {
		return contract_status_id;
	}

	public void setContract_status_id(Long contract_status_id) {
		this.contract_status_id = contract_status_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpras() {
		return spras;
	}

	public void setSpras(String spras) {
		this.spras = spras;
	}
	
	public String getName(String a_lang){
		if (a_lang.equals("en")) return this.en;
		else if (a_lang.equals("tr")) return this.tr;
		else return this.ru;
		
	}
}

