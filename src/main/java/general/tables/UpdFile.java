package general.tables;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the UPD_FILE database table.
 * 
 */
@Entity
@Table(name = "UPD_FILE")
@NamedQuery(name = "UpdFile.findAll", query = "SELECT u FROM UpdFile u")
public class UpdFile implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long created_by;

	@Temporal(TemporalType.DATE)
	private Date created_date;
	private String file_name;
	private Long file_size;

	@Id
	@SequenceGenerator(name = "UPD_FILE_ID_GENERATOR", sequenceName = "SEQ_UPD_FILE_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UPD_FILE_ID_GENERATOR")
	private Long id;

	private String mime_type;

	public UpdFile() {
	}

	public Long getCreated_by() {
		return this.created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Date getCreated_date() {
		return this.created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public String getFile_name() {
		return this.file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public Long getFile_size() {
		return this.file_size;
	}

	public void setFile_size(Long file_size) {
		this.file_size = file_size;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMime_type() {
		return this.mime_type;
	}

	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}

	@Transient
	public boolean isImage() {
		return "image/png".equals(getMime_type()) || "image/jpeg".equals(getMime_type());
	}

	@Transient
	public boolean isPdf() {
		return "application/pdf".equals(getMime_type());
	}
}