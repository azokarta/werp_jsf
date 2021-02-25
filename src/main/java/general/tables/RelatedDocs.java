package general.tables;

import java.io.Serializable;

import javax.persistence.*;


@Entity
@Table(name="RELATED_DOCS")
public class RelatedDocs implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="RELATED_DOCS_ID_GENERATOR", sequenceName="SEQ_RELATED_DOCS_ID",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RELATED_DOCS_ID_GENERATOR")
	private Long id;
	

	private String context;
	private Long context_id;
	private Long parent_id;
	private Long tree_id;
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
	public Long getParent_id() {
		return parent_id;
	}
	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}
	public Long getTree_id() {
		return tree_id;
	}
	public void setTree_id(Long tree_id) {
		this.tree_id = tree_id;
	}
	public RelatedDocs() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RelatedDocs(String context, Long context_id, Long parent_id,
			Long tree_id) {
		super();
		this.context = context;
		this.context_id = context_id;
		this.parent_id = parent_id;
		this.tree_id = tree_id;
	}
	
	
}