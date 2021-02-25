package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "MENU")
public class Menu implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "SEQ_MENU_ID_GENERATOR", sequenceName = "SEQ_MENU_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MENU_ID_GENERATOR")
	private Long id;

	private String name_ru;
	private String name_en;
	private String name_tr;
	private Long parent_id;
	private Long transaction_id;
	private String link;
	private Integer sort_order;
	private Integer tree_level;
	private Long tree_id;
	private int type;

	public Integer getTree_level() {
		return tree_level;
	}

	public void setTree_level(Integer tree_level) {
		this.tree_level = tree_level;
	}

	public Long getTree_id() {
		return tree_id;
	}

	public void setTree_id(Long tree_id) {
		this.tree_id = tree_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName_ru() {
		return name_ru;
	}

	public void setName_ru(String name_ru) {
		this.name_ru = name_ru;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getName_tr() {
		return name_tr;
	}

	public void setName_tr(String name_tr) {
		this.name_tr = name_tr;
	}

	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public Long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(Long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Integer getSort_order() {
		return sort_order;
	}

	public void setSort_order(Integer sort_order) {
		this.sort_order = sort_order;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Transient
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	private String parentName;

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	@Transient
	private String treeName;

	public String getTreeName() {
		return treeName;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

}