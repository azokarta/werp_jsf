package general.output.tables;

import general.tables.Matnr;
import general.tables.ServConMatnrWar;

public class ServConMatnrWarOutput {
	
	public ServConMatnrWarOutput() {
		// TODO Auto-generated constructor stub
		this.scMW = new ServConMatnrWar();
		this.matnr = new Matnr();
		this.group = 1;
	}
	
	public ServConMatnrWarOutput(int i) {
		// TODO Auto-generated constructor stub
		this.index = i;
		this.scMW = new ServConMatnrWar();
		this.matnr = new Matnr();
		this.group = 1;
	}
	
	int index;
	private ServConMatnrWar scMW;
	private Matnr matnr;
	private int group;
	
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

	public ServConMatnrWar getScMW() {
		return scMW;
	}

	public void setScMW(ServConMatnrWar scMW) {
		this.scMW = scMW;
	}

	public Matnr getMatnr() {
		return matnr;
	}

	public void setMatnr(Matnr matnr) {
		this.matnr = matnr;
	}
	
}
