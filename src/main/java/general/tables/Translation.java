package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "translation")
public class Translation {
	@Id
	@Column(name = "table_id")
	private Long table_id;
	
	@Column(name = "column_id")
	private Long column_id;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "spras")
	private String spras;
	
	@Column(name = "value")
	private String value;

	public Long getTable_id() {
		return table_id;
	}

	public void setTable_id(Long table_id) {
		this.table_id = table_id;
	}

	public Long getColumn_id() {
		return column_id;
	}

	public void setColumn_id(Long column_id) {
		this.column_id = column_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getSpras() {
		return spras;
	}

	public void setSpras(String spras) {
		this.spras = spras;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
