package general.output.tables;

import general.tables.ServPacketPos;

public class ServPacketPosOutput {

	public ServPacketPosOutput(int a_index) {
		this.index = a_index;
		this.spPos = new ServPacketPos();
		this.dis_mat = false;
		this.dis_qq = false;
		this.group = 1;
	}
	
	public int index;
	private ServPacketPos spPos;
	public boolean dis_mat;
	public boolean dis_qq;
	public int group;
	
	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public boolean isDis_qq() {
		return dis_qq;
	}

	public void setDis_qq(boolean dis_qq) {
		this.dis_qq = dis_qq;
	}

	public boolean isDis_mat() {
		return dis_mat;
	}

	public void setDis_mat(boolean dis_mat) {
		this.dis_mat = dis_mat;
	}

	public ServPacketPos getSpPos() {
		return spPos;
	}

	public void setSpPos(ServPacketPos spPos) {
		this.spPos = spPos;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
