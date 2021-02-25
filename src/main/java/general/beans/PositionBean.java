package general.beans;

import f4.BusinessAreaF4;
import f4.PositionF4;
import general.tables.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PositionBean implements Serializable{


	private static final long serialVersionUID = 1L;
	private String bukrs = "";
	
	private List<Position> items;
	
	public List<Position> getItems() {
		if(this.bukrs.length() > 0){
			return this.getItemsByBukrs(this.bukrs);
		}
		this.items = this.positionF4Bean.getPosition_list();
		return items;
	}
	
	public List<Position> getItemsByBukrs(String bukrsId){
		return this.positionF4Bean.getPosition_list();
	}
	
	public String getPositionLabel(Long posId){
		if(posId == null || posId == 0){
			return "";
		}
		for(Position p:this.positionF4Bean.getPosition_list()){
			if(p.getPosition_id().longValue() == posId){
				return p.getText();
			}
		}
		return "";
	}
	
	@ManagedProperty(value="#{positionF4Bean}")
	private PositionF4 positionF4Bean;
	public void setPositionF4Bean(PositionF4 positionF4Bean) {
		this.positionF4Bean = positionF4Bean;
	}
}
