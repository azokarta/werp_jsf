package hr.pyramid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.DAOException;
import general.dao.PyramidArchiveDao;
import general.tables.Bukrs;
import general.tables.PyramidArchive;
import general.tables.search.PyramidArchiveSearch;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import user.User;

@ManagedBean(name = "hrp04Bean", eager = true)
@ViewScoped
public class Hrp04 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private final static String transaction_code = "HRP04";
	private final static Long transaction_id = 105L;
	PyramidArchive rootPyramidArchive;
	String bukrs = "";
	private List<PyramidArchive> treeList;
	private PyramidArchiveSearch searchModel = new PyramidArchiveSearch();

	public PyramidArchiveSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(PyramidArchiveSearch searchModel) {
		this.searchModel = searchModel;
	}

	List<Bukrs> bukrsList = new ArrayList<Bukrs>();

	public List<Bukrs> getBukrsList() {
		return bukrsList;
	}

	@PostConstruct
	public void init() {
		try {
			PermissionController.canRead(userData, transaction_id);
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	private void preparePyramidArchiveList() {
		PyramidArchiveDao paDao = (PyramidArchiveDao) appContext.getContext().getBean("pyramidArchiveDao");
		List<PyramidArchive> rootList = new ArrayList<PyramidArchive>();
		rootList = paDao.getRootList(this.getSearchModel().getCondition());

		root = new DefaultTreeNode("Root", null);
		pyramidList = new ArrayList<PyramidArchive>();
		childList = new ArrayList<PyramidArchive>();
		if (rootList.size() > 0) {
			childList = paDao.dynamicFindPyramid(getSearchModel().getCondition() + " AND parent_pyramid_id > 0 ");
			// System.out.println("SIZE: " + childList.size());
			for (PyramidArchive pRoot : rootList) {
				TreeNode rootNode = new DefaultTreeNode(pRoot, root);

				for (PyramidArchive childNode : childList) {
					if (childNode.getParent_pyramid_id().equals(pRoot.getPyramid_id())) {
						addNodeRecursively(rootNode, childNode);
						// break;
					}
				}
			}
		}
	}

	public void search() {
		try {
			if (Validation.isEmptyString(this.getSearchModel().getBukrs())) {
				throw new DAOException("Выберите компанию");
			}
			preparePyramidArchiveList();

		} catch (DAOException ex) {
			root = new DefaultTreeNode("Root", null);
			childList = new ArrayList<PyramidArchive>();
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// ******************************************************************
	// ***************************BukrsF4*******************************

	public List<PyramidArchive> getTreeList() {
		return treeList;
	}

	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}

	// ************************PyramidArchive*************************************
	List<PyramidArchive> childList = new ArrayList<PyramidArchive>();
	List<PyramidArchive> pyramidList = new ArrayList<PyramidArchive>();

	// ************************TreeNode*********************************
	private TreeNode root = new DefaultTreeNode("Root", null);

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	private void addNodeRecursively(TreeNode parent, PyramidArchive newNode) {
		TreeNode tempNode = new DefaultTreeNode(newNode, parent);
		parent.getChildren().add(tempNode);
		for (PyramidArchive p : childList) {
			if (newNode.getPyramid_id().equals(p.getParent_pyramid_id())) {
				addNodeRecursively(tempNode, p);
			}
		}
	}

	String breadcrumb = "Отдел кадров > Иерархия > Просмотр архив иерархии";

	public String getBreadcrumb() {
		return breadcrumb;
	}
}