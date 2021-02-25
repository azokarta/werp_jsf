package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "errors")
public class Errors {
	@Id
	@Column(name = "error_id")
	private Long error_id;
	
	@Column(name = "error_text_ru")
	private String error_text_ru;
	
	@Column(name = "error_text_en")
	private String error_text_en;
	
	@Column(name = "table_id")
	private Long table_id;
	
	@Column(name = "column_id")
	private Long column_id;

	public Long getError_id() {
		return error_id;
	}

	public void setError_id(Long error_id) {
		this.error_id = error_id;
	}

	public String getError_text_ru() {
		return error_text_ru;
	}

	public void setError_text_ru(String error_text_ru) {
		this.error_text_ru = error_text_ru;
	}

	public String getError_text_en() {
		return error_text_en;
	}

	public void setError_text_en(String error_text_en) {
		this.error_text_en = error_text_en;
	}

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
	
}
