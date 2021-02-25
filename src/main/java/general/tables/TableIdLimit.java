package general.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "table_id_limit")
public class TableIdLimit implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "table_name")
	private String table_name;
	
	@Id
	@Column(name = "table_field")
	private String table_field;
	
	@Id
	@Column(name = "table_field_value")
	private String table_field_value;
	
	@Id
	@Column(name = "table_id")
	private String table_id;
	
	@Id
	@Column(name = "gjahr")
	private int gjahr;
	
	@Column(name = "from_id")
	private Long from_id;
	
	@Column(name = "to_id")
	private Long to_id;
	
	@Column(name = "current_id")
	private Long current_id;
	
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getTable_field() {
		return table_field;
	}
	public void setTable_field(String table_field) {
		this.table_field = table_field;
	}
	public String getTable_field_value() {
		return table_field_value;
	}
	public void setTable_field_value(String table_field_value) {
		this.table_field_value = table_field_value;
	}
	public String getTable_id() {
		return table_id;
	}
	public void setTable_id(String table_id) {
		this.table_id = table_id;
	}
	public int getGjahr() {
		return gjahr;
	}
	public void setGjahr(int gjahr) {
		this.gjahr = gjahr;
	}
	public Long getFrom_id() {
		return from_id;
	}
	public void setFrom_id(Long from_id) {
		this.from_id = from_id;
	}
	public Long getTo_id() {
		return to_id;
	}
	public void setTo_id(Long to_id) {
		this.to_id = to_id;
	}
	public Long getCurrent_id() {
		return current_id;
	}
	public void setCurrent_id(Long current_id) {
		this.current_id = current_id;
	}
	

}
