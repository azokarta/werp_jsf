package dit.menu;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.MenuDao;
import general.dao.TransactionDao;
import general.services.MenuService;
import general.tables.Menu;
import general.tables.Transaction;
import general.tables.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.swing.DropMode;

import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;

@ManagedBean(name = "ditMenuListBean")
@ViewScoped
public class ListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6688813646428781201L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			prepareMenuTree();
			prepareTransactionList();
		}
	}

	private List<Transaction> transactionList = new ArrayList<Transaction>();

	private void prepareTransactionList() {
		TransactionDao d = (TransactionDao) appContext.getContext().getBean(
				"transactionDao");
		transactionList = d.findAll("");
	}

	public List<Transaction> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<Transaction> transactionList) {
		this.transactionList = transactionList;
	}

	private TreeNode menuTree;

	public TreeNode getMenuTree() {
		return menuTree;
	}

	public void setMenuTree(TreeNode menuTree) {
		this.menuTree = menuTree;
	}

	private void prepareMenuTree() {
		MenuService mService = (MenuService) appContext.getContext().getBean(
				"menuService");
		menuTree = mService.getMenuTree();
	}

	private Menu selected;

	public Menu getSelected() {
		return selected;
	}

	public void setSelected(Menu selected) {
		this.selected = selected;
		prepareParents();
	}

	public Menu prepareCreate() {
		selected = new Menu();
		prepareParents();
		return selected;
	}

	public void Save() {
		try {
			if (Validation.isEmptyLong(selected.getId())) {
				Create();
			} else {
				Update();
			}

			GeneralUtil.addSuccessMessage("Сохранено успешно");
			GeneralUtil.hideDialog("MenuCreateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		MenuService s = (MenuService) appContext.getContext().getBean(
				"menuService");
		s.create(selected);
	}

	private void Update() {
		MenuService s = (MenuService) appContext.getContext().getBean(
				"menuService");
		s.update(selected);
	}

	private List<Menu> parents = new ArrayList<Menu>();

	private void prepareParents() {
		parents = new ArrayList<Menu>();
		MenuDao mDao = (MenuDao) appContext.getContext().getBean("menuDao");
		String cond = " transaction_id = 0 AND parent_id = 0 ";
		if (selected != null && !Validation.isEmptyLong(selected.getId())) {
			cond += " AND id != " + selected.getId();
		}

		List<Menu> l = mDao.findAll(cond);
		for (Menu m : l) {
			parents.add(m);
			addChilds(m.getId());
		}
	}

	private void addChilds(Long parentId) {
		String cond = " transaction_id = 0 AND parent_id = " + parentId;
		if (selected != null && !Validation.isEmptyLong(selected.getId())) {
			cond += " AND id != " + selected.getId();
		}
		MenuDao mDao = (MenuDao) appContext.getContext().getBean("menuDao");
		List<Menu> list = mDao.findAll(cond);
		for (Menu m : list) {
			m.setName_ru(" -> " + m.getName_ru());
			parents.add(m);
			addChilds(m.getId());
		}
	}

	public List<Menu> getParents() {
		return parents;
	}

	public void setParents(List<Menu> parents) {
		this.parents = parents;
	}

	private TreeNode selectedNode;

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public void onDragDrop(TreeDragDropEvent event) {
		TreeNode dragNode = event.getDragNode();
		Menu dragMenu = (Menu) dragNode.getData();
		TreeNode dropNode = event.getDropNode();
		Menu dropMenu = (Menu) dropNode.getData();
		int dropIndex = event.getDropIndex();

		MenuService s = (MenuService) appContext.getContext().getBean(
				"menuService");
		try {
			s.updateSort(dragMenu, dropMenu, dropIndex);
			GeneralUtil.addSuccessMessage("Сохранено успешно");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
