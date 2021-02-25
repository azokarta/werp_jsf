package hr.reference;

import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.PositionDao;
import general.services.PositionService;
import general.tables.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "hrReferencePositionBean")
@ViewScoped
public class HrReferencePosition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1821489082039664252L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			loadItems();
		}
	}

	List<Position> items = new ArrayList<Position>();

	public List<Position> getItems() {
		return items;
	}

	public void loadItems() {
		PositionDao d = (PositionDao) appContext.getContext().getBean(
				"positionDao");
		items = d.findAll();
	}

	private Position selected;

	public Position getSelected() {
		return selected;
	}

	public void setSelected(Position selected) {
		this.selected = selected;
		isNew = false;
	}

	private boolean isNew = false;

	public Position prepareCreate() {
		selected = new Position();
		isNew = true;
		return selected;
	}

	public void Reset() {
		selected = null;
	}

	public void Save() {
		try {
			if (selected == null) {
				throw new DAOException("selected is NULL");
			}

			if (isNew) {
				selected.setPosition_id(null);
				Create();

			} else {
				Update();
			}
			posF4Bean.updateF4();
			GeneralUtil.addSuccessMessage("Сохранено успешно");
			GeneralUtil.hideDialog("PositionCreateDlg");

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		PositionService ps = (PositionService) appContext.getContext().getBean(
				"positionService");
		ps.create(selected);
	}

	private void Update() {
		PositionService ps = (PositionService) appContext.getContext().getBean(
				"positionService");
		ps.update(selected);
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}

	@ManagedProperty(value = "#{positionF4Bean}")
	private PositionF4 posF4Bean;

	public PositionF4 getPosF4Bean() {
		return posF4Bean;
	}

	public void setPosF4Bean(PositionF4 posF4Bean) {
		this.posF4Bean = posF4Bean;
	}

}
