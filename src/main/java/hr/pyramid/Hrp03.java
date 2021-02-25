package hr.pyramid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.tables.Bukrs;
import general.tables.Pyramid;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import user.User;

@ManagedBean(name = "hrp03Bean", eager = true)
@ViewScoped
public class Hrp03 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5816878890675838063L;
	/**
	 * 
	 */
	private final static String transaction_code = "HRP03";
	private final static Long transaction_id = (long) 30;
	Pyramid rootPyramid;
	String bukrs = "";
	private List<Pyramid> treeList;

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	List<Bukrs> bukrsList = new ArrayList<Bukrs>();

	public List<Bukrs> getBukrsList() {
		return bukrsList;
	}

	@PostConstruct
	public void init() {
		try {
			PermissionController.canRead(userData, transaction_id);
			// System.out.println("CHECK PERMISSIOn");
			if (!userData.isMain() && !userData.isSys_admin()) {
				setBukrs(userData.getBukrs());
			}
			setHasPermissionToUpdate(PermissionController.canAll(userData, transaction_id));
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	boolean hasPermissionToUpdate = true;

	public boolean isHasPermissionToUpdate() {
		return hasPermissionToUpdate;
	}

	public void setHasPermissionToUpdate(boolean hasPermissionToUpdate) {
		this.hasPermissionToUpdate = hasPermissionToUpdate;
	}

	private void preparePyramidList() {
		PyramidDao pyrDao = (PyramidDao) appContext.getContext().getBean("pyramidDao");

		List<Pyramid> pList = pyrDao.findAllWithDetailsByBukrs(bukrs);
		root = new DefaultTreeNode("Root", null);

		for (Pyramid pyr : pList) {
			if (pyr.getParent_pyramid_id() == 0) {
				TreeNode rootNode = new DefaultTreeNode(pyr, root);
				addNodeRecursively(rootNode, pyr.getPyramid_id(), pList);
			}
		}
	}

	private void addNodeRecursively(TreeNode parent, Long parentId, List<Pyramid> pyrList) {
		for (Pyramid pyr : pyrList) {
			if (!Validation.isEmptyLong(pyr.getParent_pyramid_id()) && pyr.getParent_pyramid_id().equals(parentId)) {
				TreeNode tempNode = new DefaultTreeNode(pyr, parent);
				parent.getChildren().add(tempNode);
				addNodeRecursively(tempNode, pyr.getPyramid_id(), pyrList);
			}
		}
	}

	public void search() {
		try {

			// StaffService staffService = (StaffService)
			// appContext.getContext().getBean("staffService");
			if (bukrs.length() == 0) {
				throw new DAOException("Выберите компанию");
			}

			preparePyramidList();

		} catch (DAOException ex) {
			root = new DefaultTreeNode("Root", null);
			childList = new ArrayList<Pyramid>();
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// ******************************************************************
	// ***************************BukrsF4*******************************

	public List<Pyramid> getTreeList() {
		return treeList;
	}

	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}

	// ************************Pyramid*************************************
	List<Pyramid> childList = new ArrayList<Pyramid>();
	List<Pyramid> pyramidList = new ArrayList<Pyramid>();

	// ************************TreeNode*********************************
	private TreeNode root = new DefaultTreeNode("Root", null);

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	String breadcrumb = "Отдел кадров > Иерархия > Просмотр иерархия";

	public String getBreadcrumb() {
		return breadcrumb;
	}

	public boolean isRenderCreate(Long positionId) {
		if (Validation.isEmptyLong(positionId)) {
			return false;
		}
		return (positionId.longValue() == 4 || positionId.longValue() == 8 || positionId.longValue() == 9) ? false
				: true;
	}
}