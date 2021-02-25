package logistics.werks;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.search.MatnrListSearch;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "lw03Bean")
@ViewScoped
public class Lw03 implements Serializable {


	@PostConstruct
	public void init() {
		// TODO PERMISSION

		if (GeneralUtil.isAjaxRequest()) {
			return;
		}

		try {
			if (matnrId != null && matnrId.longValue() > 0) {
				loadMatnr();
				//searchModel.setBukrs(selectedMatnr.getBukrs());
				searchModel.setWerks(werksId);
				searchModel.setMatnr(matnrId);
				loadItems();
			}

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private MatnrListSearch searchModel = new MatnrListSearch();

	public MatnrListSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(MatnrListSearch searchModel) {
		this.searchModel = searchModel;
	}

	private Long werksId = null;
	private Long matnrId = null;

	public Long getWerksId() {
		return werksId;
	}

	public void setWerksId(Long werksId) {
		this.werksId = werksId;
	}

	public Long getMatnrId() {
		return matnrId;
	}

	public void setMatnrId(Long matnrId) {
		this.matnrId = matnrId;
	}

	private Matnr selectedMatnr;

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void loadMatnr() {
		if(selectedMatnr != null || matnrId == null){
			return;
		}
		MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
		selectedMatnr = d.find(matnrId);
		if (selectedMatnr == null) {
			throw new DAOException("Материал не найден");
		}
	}

	private List<MatnrList> items;

	public List<MatnrList> getItems() {
		return items;
	}

	private void loadItems() {
		System.out.println(searchModel.getCondition());
		MatnrListDao d = (MatnrListDao) appContext.getContext().getBean(
				"matnrListDao");
		items = d.dynamicFind3(searchModel.getCondition());
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
