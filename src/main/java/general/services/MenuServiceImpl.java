package general.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.DAOException;
import general.dao.MenuDao;
import general.dao.TransactionDao;
import general.tables.Menu;
import general.tables.Transaction;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("menuService")
public class MenuServiceImpl implements MenuService {

	@Autowired
	MenuDao mDao;

	@Autowired
	TransactionDao transactionDao;

	@Override
	public void create(Menu m) throws DAOException {
		String error = validate(m);
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		m.setId(null);
		m.setTree_level(0);
		m.setTree_id(0L);
		mDao.create(m);
		setTreeId(m);
	}

	private void setTreeId(Menu m) {
		if (Validation.isEmptyLong(m.getParent_id())) {
			m.setTree_id(m.getId());
			m.setTree_level(0);
		} else {
			Menu parentMenu = mDao.find(m.getParent_id());
			if (parentMenu != null) {
				m.setTree_id(parentMenu.getTree_id());
				m.setTree_level(parentMenu.getTree_level()+1);
			}
		}

		mDao.update(m);
	}

	private String validate(Menu m) {
		String error = "";

		if (Validation.isEmptyString(m.getName_ru())) {
			error += "Название на рус обязательно для заполнения \n";
		}

		if (Validation.isEmptyLong(m.getTransaction_id())) {
			m.setTransaction_id(0L);
		}

		List<Menu> tempList = mDao.findAll("parent_id = " + m.getParent_id()
				+ " ORDER BY sort_order ASC ");
		if (tempList != null && tempList.size() > 0) {
			Integer sOrder = tempList.get(tempList.size() - 1).getSort_order();
			m.setSort_order(sOrder + 1);
		} else {
			m.setSort_order(0);
		}

		return error;
	}

	@Override
	public void update(Menu m) throws DAOException {
		String error = validate(m);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		mDao.update(m);
		setTreeId(m);
	}

	private void addNode(Long parentId, TreeNode parentNode,
			Map<Long, Transaction> trMap) {
		List<Menu> menuList = mDao.findAll(" parent_id = " + parentId
				+ " ORDER BY SORT_ORDER ASC ");
		for (Menu m : menuList) {
			Transaction tr = trMap.get(m.getTransaction_id());
			if (tr != null) {
				m.setLink(tr.getUrl());
			}
			TreeNode tn = new DefaultTreeNode(m, parentNode);
			addNode(m.getId(), tn, trMap);
		}
	}

	@Override
	public TreeNode getMenuTree() {
		Map<Long, Transaction> trMap = new HashMap<Long, Transaction>();
		for (Transaction tr : transactionDao.findAll("")) {
			trMap.put(tr.getTransaction_id(), tr);
		}
		TreeNode root = new DefaultTreeNode(new Menu(), null);
		addNode(0L, root, trMap);
		return root;
	}

	@Override
	public void updateSort(Menu childMenu, Menu parentMenu, int sortOrder)
			throws DAOException {

		Long newParentId = 0L;
		if (parentMenu != null && !Validation.isEmptyLong(parentMenu.getId())) {
			newParentId = parentMenu.getId();
		}

		boolean isFound = false;
		List<Menu> sibleMenus = mDao.findAll(String.format(
				" parent_id = %d AND id != %d ORDER BY sort_order ASC ",
				childMenu.getParent_id(), childMenu.getId()));
		for (int i = 0; i < sibleMenus.size(); i++) {
			Menu sm = sibleMenus.get(i);
			if (i == sortOrder && childMenu.getParent_id().equals(newParentId)) {
				isFound = true;
			}

			if (isFound) {
				sm.setSort_order(i + 1);
			} else {
				sm.setSort_order(i);
			}
			mDao.update(sm);
		}

		if (!childMenu.getParent_id().equals(newParentId)) {

			List<Menu> newSibleMenus = mDao.findAll("parent_id = "
					+ newParentId + " ORDER BY sort_order ASC ");

			isFound = false;
			for (int i = 0; i < newSibleMenus.size(); i++) {
				Menu nsm = newSibleMenus.get(i);
				if (i == sortOrder) {
					isFound = true;
				}

				if (isFound) {
					nsm.setSort_order(i + 1);
				} else {
					nsm.setSort_order(i);
				}

				mDao.update(nsm);

			}
		}

		childMenu.setSort_order(sortOrder);
		childMenu.setParent_id(newParentId);
		setTreeId(childMenu);
	}

}