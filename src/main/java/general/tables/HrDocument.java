package general.tables;

import java.io.Serializable;

import javax.persistence.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistent class for the HR_DOCUMENT database table.
 * 
 */
@Entity
@Table(name = "HR_DOCUMENT")
public class HrDocument implements Serializable, IMdTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1514832918462608488L;

	@Id
	@SequenceGenerator(name = "HR_DOCUMENT_ID_GENERATOR", sequenceName = "SEQ_HR_DOCUMENT_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HR_DOCUMENT_ID_GENERATOR")
	private Long id;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	private String bukrs;

	@Column(name = "CREATED_AT")
	private Date createdAt;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Column(name = "STATUS_ID")
	private Integer statusId;

	@Column(name = "TYPE_ID")
	private Integer typeId;

	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Column(name = "RESPONSIBLE_ID")
	private Long responsibleId;

	@Column(name = "CURRENT_RESP_ID")
	private Long currentRespId;

	@Column(name = "REG_NUMBER")
	private Integer regNumber;

	public Long getCurrentRespId() {
		return currentRespId;
	}

	public void setCurrentRespId(Long currentRespId) {
		this.currentRespId = currentRespId;
	}

	public HrDocument() {
		this.statusId = STATUS_ON_CREATE;
		setResponsibleId(47L);// Bahtiyar HR костыль
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBranchId() {
		return this.branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getBukrs() {
		return this.bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
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

	public Integer getStatusId() {
		return this.statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
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

	public Long getResponsibleId() {
		return responsibleId;
	}

	public void setResponsibleId(Long responsibleId) {
		this.responsibleId = responsibleId;
	}

	public Integer getRegNumber() {
		return regNumber;
	}

	public void setRegNumber(Integer regNumber) {
		this.regNumber = regNumber;
	}

	@OneToMany(mappedBy = "hrDocument", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	List<HrDocumentItem> items;

	public List<HrDocumentItem> getItems() {
		return items;
	}

	public void setItems(List<HrDocumentItem> items) {
		this.items = items;
	}

	public void addItem(HrDocumentItem hdi) {
		if (hdi != null) {
			if (items == null) {
				items = new ArrayList<>();
			}
			if (!items.contains(hdi)) {
				items.add(hdi);
				if (hdi.getHrDocument() != this) {
					hdi.setHrDocument(this);
				}
			}
		}
	}

	@OneToMany(mappedBy = "hrDocument", orphanRemoval = true, cascade = CascadeType.ALL)
	// @Cascade(value = { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	List<HrDocumentRoute> routes = new ArrayList<>();

	public List<HrDocumentRoute> getRoutes() {
		return routes;
	}

	public void setRoutes(List<HrDocumentRoute> routes) {
		this.routes = routes;
	}

	public void addRoute(HrDocumentRoute hdr) {
		if (hdr != null) {
			if (routes == null) {
				routes = new ArrayList<>();
			}
			if (!routes.contains(hdr)) {
				this.routes.add(hdr);
				hdr.setHrDocument(this);
			}
		}

	}

	@OneToMany(mappedBy = "hrDocument", cascade = CascadeType.MERGE, orphanRemoval = true)
	Set<HrDocumentActionLog> actionLogs = new HashSet<HrDocumentActionLog>();

	public Set<HrDocumentActionLog> getActionLogs() {
		return actionLogs;
	}

	public void setActionLogs(Set<HrDocumentActionLog> actionLogs) {
		this.actionLogs = actionLogs;
	}

	public void addActionLog(HrDocumentActionLog log) {
		// if (!this.actionLogs.contains(log)) {
		actionLogs.add(log);
		log.setHrDocument(this);
		// }
	}

	public boolean isUpdatable() {
		return getStatusId().equals(HrDocument.STATUS_ON_CREATE);
	}

	public static final Integer STATUS_ON_CREATE = 1; // На создании
	public static final Integer STATUS_ON_VIEW = 2; // На просмотре
	public static final Integer STATUS_ON_AGREEMENT = 3; // На согласовании
	public static final Integer STATUS_ON_EXECUTION = 4; // На исполнении
	public static final Integer STATUS_CLOSED = 5;// Закрытый
	public static final Integer STATUS_REFUSED = 6;// Отказан

	public String getStatusName() {
		if (STATUS_ON_AGREEMENT.equals(getStatusId())) {
			return "НА СОГЛАСОВАНИИ";
		} else if (STATUS_ON_CREATE.equals(getStatusId())) {
			return "НА СОЗДАНИИ";
		} else if (STATUS_ON_EXECUTION.equals(getStatusId())) {
			return "НА ИСПОЛНЕНИИ";
		} else if (STATUS_REFUSED.equals(getStatusId())) {
			return "ОТКАЗАН";
		} else if (STATUS_CLOSED.equals(getStatusId())) {
			return "ЗАКРЫТ";
		}
		return null;
	}

	public static final Integer TYPE_RECRUITMENT = 1;
	public static final Integer TYPE_TRANSFER = 2;
	public static final Integer TYPE_DISMISS = 3;

	public static final Integer ACTION_CREATE = 1;
	public static final Integer ACTION_UPDATE = 2;
	public static final Integer ACTION_VIEW = 3;
	public static final Integer ACTION_SEND = 4;// Отправить на просмотр
												// Бахтияру
	public static final Integer ACTION_AGREEMENT = 5;
	public static final Integer ACTION_REFUSE = 6;
	public static final Integer ACTION_ADD_ROUTE = 7;// Добавление согласующих
	public static final Integer ACTION_SEND_TO_EXECUTION = 8;// Отправить на
																// исполнение
	public static final Integer ACTION_SEND_TO_AGREEMENT = 9;// Отправить на
																// согласование

	public static final Integer ACTION_CREATE_NEW_SALARY = 10;
	public static final Integer ACTION_ADD_AMOUNT = 11;

	public static final String CONTEXT = "hr_document";
	public static final String CONTEXT_RECRUITMENT = "hr_document_rec";
	public static final String CONTEXT_TRANSFER = "hr_document_trans";
	public static final String CONTEXT_DISMISS = "hr_document_dismiss";

	public static Map<Integer, String> getActionMap() {
		Map<Integer, String> m = new HashMap<Integer, String>();
		m.put(ACTION_ADD_ROUTE, "Добавление согласующего");
		m.put(ACTION_AGREEMENT, "Согласовать");
		m.put(ACTION_CREATE, "Создание");
		m.put(ACTION_REFUSE, "Отказать");
		m.put(ACTION_SEND, "Отправка");
		m.put(ACTION_SEND_TO_AGREEMENT, "Отправка на согласование");
		m.put(ACTION_SEND_TO_EXECUTION, "Отправка на исполнение");
		m.put(ACTION_UPDATE, "Редактирование");
		m.put(ACTION_VIEW, "Просмотр");
		return m;
	}

	public String getTypeName() {
		if (getTypeId().equals(HrDocument.TYPE_RECRUITMENT)) {
			return "Заявление о приеме на работу";
		}else if (getTypeId().equals(HrDocument.TYPE_DISMISS)) {
			return "Заявление об уволнении";
		}else if (getTypeId().equals(HrDocument.TYPE_TRANSFER)) {
			return "Заявление о переводе";
		}
		return null;
	}

	@Override
	public String getContext() {
		if (getTypeId().equals(HrDocument.TYPE_RECRUITMENT)) {
			return HrDocument.CONTEXT_RECRUITMENT;
		} else if (getTypeId().equals(HrDocument.TYPE_TRANSFER)) {
			return HrDocument.CONTEXT_TRANSFER;
		} else if (getTypeId().equals(HrDocument.TYPE_DISMISS)) {
			return HrDocument.CONTEXT_DISMISS;
		}
		return HrDocument.CONTEXT;
	}

	@Override
	public Long getContextId() {
		return getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		HrDocument other = (HrDocument) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Transient
	private Staff creator;

	public Staff getCreator() {
		return creator;
	}

	public void setCreator(Staff creator) {
		this.creator = creator;
	}

	@Transient
	private Staff responsible;

	public Staff getResponsible() {
		return responsible;
	}

	public void setResponsible(Staff responsible) {
		this.responsible = responsible;
	}

	@Transient
	public String getFormattedRegNumber(){
		return String.format("%07d", getRegNumber());
	}
}