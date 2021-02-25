package general.beans;

import f4.BusinessAreaF4;
import general.tables.BusinessArea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class BusinessAreaBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<BusinessArea> items;
	private String bukrs = "";
	
	public List<BusinessArea> getItems() {
		if(this.bukrs.length() > 0){
			return this.getItemsByBukrs(this.bukrs);
		}
		this.items = this.businessAreaF4Bean.getL_ba();
		return items;
	}

	public List<BusinessArea> getItemsByBukrs(String bukrsId){
		List<BusinessArea> out = new ArrayList<BusinessArea>();
		for(BusinessArea ba:this.businessAreaF4Bean.getL_ba()){
			if(ba.getBukrs().equals(bukrsId)){
				out.add(ba);
			}
		}
		
		return out;
	}
	
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	
	
	@ManagedProperty(value="#{businessAreaF4Bean}")
	private BusinessAreaF4 businessAreaF4Bean;
	public void setBusinessAreaF4Bean(BusinessAreaF4 businessAreaF4Bean) {
		this.businessAreaF4Bean = businessAreaF4Bean;
	}
}
