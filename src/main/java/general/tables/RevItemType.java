package general.tables;

import java.util.List;

public class RevItemType {

	public final static int TYPE_MATNR = 1;
	public final static int TYPE_PART = 2;

	private int id;
	private String name;
	private List<Long> matnrIdList;
	private int parentId;
	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Long> getMatnrIdList() {
		return matnrIdList;
	}

	public void setMatnrIdList(List<Long> matnrIdList) {
		this.matnrIdList = matnrIdList;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

}
