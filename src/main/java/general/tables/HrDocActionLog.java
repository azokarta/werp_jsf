package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import general.MessageProvider;

import java.util.Calendar;
import java.util.Date;

/**
 * The persistent class for the HR_DOCUMENT_ACTION_LOG database table.
 * 
 */
@Entity
@Table(name = "HR_DOC_ACTION_LOG")
public class HrDocActionLog implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Integer ACTION_CREATE = 1;
	public static final Integer ACTION_UPDATE = 2;
	public static final Integer ACTION_VIEW = 3;
	public static final Integer ACTION_SEND = 4;// Отправить на просмотр
												// Бахтияру
	public static final Integer ACTION_APPROVE = 5;
	public static final Integer ACTION_REFUSE = 6;
	public static final Integer ACTION_ADD_APPROVER = 7;// Добавление
														// согласующих
	public static final Integer ACTION_SEND_TO_EXECUTION = 8;// Отправить на
																// исполнение
	public static final Integer ACTION_SEND_TO_APPROVER = 9;// Отправить на
															// согласование

	public static final Integer ACTION_CREATE_NEW_SALARY = 10;
	public static final Integer ACTION_ADD_AMOUNT = 11;
	public static final Integer ACTION_ADD_SALARY = 12;// Добавить Должности
	public static final Integer ACTION_DISMISS_EMPLOYEE = 13;// Уволить
																// сотрудников
	public static final Integer ACTION_CANCEL = 14;// Отменить

	public static final Integer ACTION_COMPLETE_DOC = 15;// Завершить

	public static final Integer ACTION_CREATE_BYPASS_SHEET = 16;// Создать
																// обходной лист

	@Id
	@SequenceGenerator(name = "SEQ_HR_DOC_ACTION_LOG_ID", sequenceName = "SEQ_HR_DOC_ACTION_LOG_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HR_DOC_ACTION_LOG_ID")
	private Long id;

	@Column(name = "ACTION_ID")
	private Integer actionId;

	@Column(name = "NOTE")
	private String note;

	@Column(name = "DOC_ID")
	private Long docId;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_AT")
	private Date createdAt;

	public HrDocActionLog() {
	}

	public HrDocActionLog(Integer actionId, String note, Long docId, Long createdBy) {
		super();
		this.actionId = actionId;
		this.note = note;
		this.docId = docId;
		this.createdBy = createdBy;
		this.createdAt = Calendar.getInstance().getTime();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public static String getName(Integer actionId) {
		MessageProvider mp = new MessageProvider();
		if (ACTION_CANCEL.equals(actionId)) {
			//return "***ОТМЕНИТЬ***";
			return mp.getValue("hr.action.cancel");
		} else if (ACTION_UPDATE.equals(actionId)) {
			//return "Редактировать";
			return mp.getValue("hr.action.update");
		} else if (ACTION_SEND.equals(actionId)) {
			//return "Отправить";
			return mp.getValue("hr.action.send");
		} else if (ACTION_ADD_APPROVER.equals(actionId)) {
			//return "Добавить согласующих";
			return mp.getValue("hr.action.add_approvers");
		} else if (ACTION_CREATE.equals(actionId)) {
			//return "Создание";
			return mp.getValue("hr.action.create");
		} else if (ACTION_VIEW.equals(actionId)) {
			//return "Просмотр";
			return mp.getValue("hr.action.view");
		} else if (ACTION_ADD_AMOUNT.equals(actionId)) {
			//return "Проставить оклады";
			return mp.getValue("hr.action.set_amount");
		} else if (ACTION_APPROVE.equals(actionId)) {
			//return "Согласовать";
			return mp.getValue("hr.action.approve");
		} else if (ACTION_REFUSE.equals(actionId)) {
			//return "Отказать";
			return mp.getValue("hr.action.reject");
		} else if (ACTION_ADD_SALARY.equals(actionId)) {
			//return "Добавить должности";
			return mp.getValue("hr.action.add_salaries");
		} else if (ACTION_COMPLETE_DOC.equals(actionId)) {
			//return "Завершить";
			return mp.getValue("hr.action.complete");
		}else if(ACTION_CREATE_BYPASS_SHEET.equals(actionId)){
			return "Создать обходной лист";
		}
		return "";
	}

	@Transient
	private Staff creator;

	// @ManyToOne
	// @JoinColumn(name = "hr_document_id")
	// @OrderBy(" created_at DESC ")
	// private HrDocument hrDocument;
	//
	// public HrDocument getHrDocument() {
	// return hrDocument;
	// }
	//
	// public void setHrDocument(HrDocument hrDocument) {
	// this.hrDocument = hrDocument;
	// }

	// @OneToOne
	// @JoinColumn(name = "created_by")
	// private User user;
	//
	// public User getUser() {
	// return user;
	// }
	//
	// public void setUser(User user) {
	// this.user = user;
	// }

	public Staff getCreator() {
		return creator;
	}

	public void setCreator(Staff creator) {
		this.creator = creator;
	}

	public String getActionName() {
		return HrDocActionLog.getName(getActionId());
	}

}