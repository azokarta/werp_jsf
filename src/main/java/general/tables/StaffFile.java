package general.tables;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "STAFF_FILE")
@javax.persistence.SequenceGenerator(name="SEQ_STAFF_FILE_ID",sequenceName="SEQ_STAFF_FILE_ID",allocationSize=1)
public class StaffFile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SEQ_STAFF_FILE_ID")
    private Long id;
	
	private Long staff_id;
	private Long file_id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getStaff_id() {
		return staff_id;
	}
	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}
	public Long getFile_id() {
		return file_id;
	}
	public void setFile_id(Long file_id) {
		this.file_id = file_id;
	}
	
	
	
}
