package dms.contract;

import general.tables.Bkpf;

public class PaymentHistory {
	
	PaymentHistory() {
		this.bkpf = new Bkpf();
		this.info = "";
	}
	
	PaymentHistory(int i) {
		this.index = i;
		this.bkpf = new Bkpf();
		this.info = "";
	}
		
	int index;
	Bkpf bkpf;
	String info;
	int group;
	
	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Bkpf getBkpf() {
		return bkpf;
	}

	public void setBkpf(Bkpf bkpf) {
		this.bkpf = bkpf;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
