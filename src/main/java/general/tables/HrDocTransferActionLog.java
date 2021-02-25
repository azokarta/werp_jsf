package general.tables;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the HR_DOC_TRANSFER_ACTION_LOG database table.
 * 
 */
@Entity
@Table(name="HR_DOC_TRANSFER_ACTION_LOG")
@NamedQuery(name="HrDocTransferActionLog.findAll", query="SELECT h FROM HrDocTransferActionLog h")
public class HrDocTransferActionLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACTION_ID")
	private Integer actionId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_AT")
	private Date createdAt;

	@Column(name="CREATED_BY")
	private Long createdBy;

	@Id
	@SequenceGenerator(name="HR_DOC_TRANS_ACT_LOG_ID_GENERATOR", sequenceName="SEQ_HR_DOC_TRANS_ACT_LOG_ID", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="HR_DOC_TRANS_ACT_LOG_ID_GENERATOR")
	private Long id;

	//bi-directional many-to-one association to HrDocTransfer
	@ManyToOne
	@JoinColumn(name="DOC_ID", referencedColumnName="ID")
	private HrDocTransfer hrDocTransfer;

	public HrDocTransferActionLog() {
	}

	public Integer getActionId() {
		return this.actionId;
	}

	public void setActionId(Integer actionId) {
		this.actionId = actionId;
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

	public HrDocTransfer getHrDocTransfer() {
		return this.hrDocTransfer;
	}

	public void setHrDocTransfer(HrDocTransfer hrDocTransfer) {
		this.hrDocTransfer = hrDocTransfer;
	}

}