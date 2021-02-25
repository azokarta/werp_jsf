package general.tables;

import general.Validation;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "MY_DOCS")
public class MyDocs implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "MY_DOCS_ID_GENERATOR", sequenceName = "SEQ_MY_DOCS_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MY_DOCS_ID_GENERATOR")
	private Long id;

	private String context;
	private Long context_id;
	private Long owner;
	private Integer status_id;
	private Date created_at;
	private Long created_by;
	private String bukrs;
	private Long branch_id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Long getContext_id() {
		return context_id;
	}

	public void setContext_id(Long context_id) {
		this.context_id = context_id;
	}

	public Long getOwner() {
		return owner;
	}

	public void setOwner(Long owner) {
		this.owner = owner;
	}

	public Integer getStatus_id() {
		return status_id;
	}

	public void setStatus_id(Integer status_id) {
		this.status_id = status_id;
	}

	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public static final Integer STATUS_CREATE = 1;// Созданные
	public static final Integer STATUS_IN = 2;// Входящие
	public static final Integer STATUS_SENT = 3;// Отправленные
	public static final Integer STATUS_CONFIRMED = 4;// Согласованные
	public static final Integer STATUS_CLOSED = 5;// Закрыт
	public static final Integer STATUS_REFUSED = 6;//

	public Map<Integer, String> getStatuses() {
		Map<Integer, String> out = new HashMap<Integer, String>();
		out.put(STATUS_CREATE, "Созданные");
		out.put(STATUS_IN, "Входящие");
		out.put(STATUS_SENT, "Отправленные");
		out.put(STATUS_CONFIRMED, "Согласованные");
		out.put(STATUS_CLOSED, "Закрыт");
		return out;
	}

	public MyDocs() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MyDocs(String context, Long context_id, Long owner, Integer status_id) {
		super();
		this.context = context;
		this.context_id = context_id;
		this.owner = owner;
		this.status_id = status_id;
	}

	public String getLink() {
		// return link;
		if (!Validation.isEmptyString(getContext())) {
			if (getContext().equals("request")) {
				return "/request/View.xhtml?id=" + getContext_id();
			}
		}

		return "";
	}

	public String getDocumentName() {
		if (!Validation.isEmptyString(getContext())) {
			if (getContext().equals("request")) {
				return "Заявки";
			}
		}

		return "";
	}

	@Transient
	private Request request;

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	@Transient
	private Staff creator;

	public Staff getCreator() {
		return creator;
	}

	public void setCreator(Staff creator) {
		this.creator = creator;
	}

}