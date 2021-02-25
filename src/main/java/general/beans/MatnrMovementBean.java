package general.beans;

import general.AppContext;
import general.dao.MatnrMovementDao;
import general.tables.MatnrMovement;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class MatnrMovementBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MatnrMovement> items;
	private MatnrMovement selected;

	public MatnrMovement getSelected() {
		return selected;
	}

	public void setSelected(MatnrMovement selected) {
		this.selected = selected;
	}

	public List<MatnrMovement> getItems() {
		MatnrMovementDao d = (MatnrMovementDao) appContext.getContext()
				.getBean("matnrMovementDao");
		this.items = d.findAll("");
		return items;
	}

	public List<MatnrMovement> getMovingItems() {
		MatnrMovementDao d = (MatnrMovementDao) appContext.getContext()
				.getBean("matnrMovementDao");
		return d.findAll(String.format(
				" status = '%s' AND mm_type IN('%s','%s') ",
				MatnrMovement.STATUS_MOVING, MatnrMovement.TYPE_MOVEMENT_SEND,
				MatnrMovement.TYPE_MOVEMENT_RECEIVE));
	}

	public void removeSelected() {
		this.selected = null;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
