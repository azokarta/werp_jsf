package logistics;

import general.GeneralUtil;
import general.dao.DAOException;
import general.tables.MatnrList;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="lgMatnrBean")
@ViewScoped
public class LgMatnr extends LgBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6771556658942952873L;
	
	@PostConstruct
	public void init(){
		try{
			matnrListId = Long.valueOf(GeneralUtil.getRequestParameter("matnrListId"));
		}catch(Exception e){
			matnrListId = 0L;
		}
		
		loadSelected();
	}

	private Long matnrListId;
	private MatnrList selected;
	public MatnrList getSelected() {
		return selected;
	}
	private void loadSelected(){
		selected = getMatnrListDao().find(matnrListId);
		if(selected == null){
			throw new DAOException("Материал не найден!");
		}
	}
	
	
	private String pageHeader;
	public String getPageHeader() {
		return pageHeader;
	}
	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}
	
}
