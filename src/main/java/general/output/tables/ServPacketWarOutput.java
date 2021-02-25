package general.output.tables;

import general.tables.ServPacketWar;

public class ServPacketWarOutput {

	public ServPacketWarOutput() {
		this.spWar = new ServPacketWar();				
	}
	
	public ServPacketWarOutput(int i) {
		this.index = i;
		this.spWar = new ServPacketWar();
		this.group = 1;		
	}
	
	public int index;
	private ServPacketWar spWar;
	public int group;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public ServPacketWar getSpWar() {
		return spWar;
	}
	public void setSpWar(ServPacketWar spWar) {
		this.spWar = spWar;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
}
