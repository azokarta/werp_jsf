package general.tables;

import general.Validation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "matnr")
@javax.persistence.SequenceGenerator(name = "seq_matnr_id", sequenceName = "seq_matnr_id", allocationSize = 1)
public class Matnr implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2034602526145613753L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_matnr_id")
	@Column(name = "matnr")
	private Long matnr;

	@Column(name = "text45")
	private String text45;

	@Column(name = "name_en")
	private String name_en;

	@Column(name = "name_tr")
	private String name_tr;

	@Column(name = "spras")
	private String spras;

	@Column(name = "type")
	private int type;

	@Column(name = "status")
	private int status;

	@Column(name = "parent_matnr")
	private Long parent_matnr;

	@Column(name = "code")
	private String code;

	@Column(name = "comment2")
	private String comment;

	private Double account_limit;

	// @OneToMany(mappedBy="rmatnr",cascade = CascadeType.ALL)
	// private List<PriceList> l_priceList;

	// public List<PriceList> getL_priceList() {
	// return l_priceList;
	// }

	public Matnr() {
		this.fno = 0;
		this.is_m = 0;
		this.account_limit = 999D;
	}

	@Column(name = "category")
	private Long category;

	public static int CAT_VC = 1;
	public static int CAT_WF = 2;
	public static int CAT_AC = 3;

	public Long getCategory() {
		return category;
	}

	public void setCategory(Long category) {
		this.category = category;
	}

	private int is_m;
	private int fno;

	public int getFno() {
		return fno;
	}

	public void setFno(int fno) {
		this.fno = fno;
	}

	public int getIs_m() {
		return is_m;
	}

	public void setIs_m(int is_m) {
		this.is_m = is_m;
	}

	private Integer min_stock;

	public Integer getMin_stock() {
		return min_stock;
	}

	public void setMin_stock(Integer min_stock) {
		this.min_stock = min_stock;
	}

	public Long getParent_matnr() {
		return parent_matnr;
	}

	public void setParent_matnr(Long parent_matnr) {
		this.parent_matnr = parent_matnr;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public String getText45() {
		return text45;
	}

	public void setText45(String text45) {
		if (Validation.isEmptyString(text45)) {
			this.text45 = text45;
		} else {
			this.text45 = text45.trim();
		}
	}

	public String getSpras() {
		return spras;
	}

	public void setSpras(String spras) {
		this.spras = spras;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		if (Validation.isEmptyString(code)) {
			this.code = code;
		} else {
			this.code = code.trim();
		}
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Double getAccount_limit() {
		return account_limit;
	}

	public void setAccount_limit(Double account_limit) {
		this.account_limit = account_limit;
	}
	
	public String getNameByLang (String lang){
		if("en".equals(lang)){
			return name_en;
		} else if("tr".equals(lang)){
			return name_tr;
		}
		
		return text45;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matnr == null) ? 0 : matnr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Matnr other = (Matnr) obj;
		if (matnr == null) {
			if (other.matnr != null)
				return false;
		} else if (!matnr.equals(other.matnr))
			return false;
		return true;
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

	public String getName(String lang) {
		if (lang.equals("en") && this.name_en != null && this.name_en.length() > 0) {
			return this.name_en;
		} else if (lang.equals("tr") && this.name_tr != null && this.name_tr.length() > 0) {
			return this.name_tr;
		}

		return this.text45;
	}

}
