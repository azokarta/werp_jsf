package general.tables;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the REV_ITEM_TITLE database table.
 * 
 */
@Entity
@Table(name="REV_ITEM_TITLE")
@NamedQuery(name="RevItemTitle.findAll", query="SELECT r FROM RevItemTitle r")
public class RevItemTitle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_AT")
	private Date createdAt;

	@Column(name="CREATED_BY")
	private Long createdBy;

	@Id
	@SequenceGenerator(name="REV_ITEM_TITLE_ID_GENERATOR", sequenceName="SEQ_REV_ITEM_ID",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REV_ITEM_TITLE_ID_GENERATOR")
	private Long id;

	//private Long matnr;

	@Column(name="REV_ID")
	private Long revId;

	private String title;

	@Column(name="TYPE_ID")
	private Integer typeId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_AT")
	private Date updatedAt;

	@Column(name="UPDATED_BY")
	private Long updatedBy;

	public RevItemTitle() {
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Long getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public Long getMatnr() {
//		return this.matnr;
//	}
//
//	public void setMatnr(Long matnr) {
//		this.matnr = matnr;
//	}

	public Long getRevId() {
		return this.revId;
	}

	public void setRevId(Long revId) {
		this.revId = revId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

}