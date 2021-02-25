package general;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import user.User;
import general.dao.MenuDao;
import general.dao.TransactionDao;
import general.tables.Menu;
import general.tables.Transaction;

;

@ManagedBean(name = "mpmtBean", eager = true)
@SessionScoped
public class MainPageTreeMenu implements Serializable {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TreeNode root;
	private TreeNode selectedNode;

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@PostConstruct
	public void init() {
		prepareTransactions();
		prepareMenus();
	}

	private void prepareMenus() {
		String[] trIds = new String[transactionList.size()];
		for (int i = 0; i < transactionList.size(); i++) {
			trIds[i] = transactionList.get(i).getTransaction_id().toString();
		}
		MenuDao menuDao = (MenuDao) appContext.getContext().getBean("menuDao");
		if (trIds.length > 0) {
			List<Menu> mList = menuDao
					.findAll(String.format(" transaction_id IN(%s) ", "'" + String.join("','", trIds) + "'"));
			String[] treeIds = new String[mList.size()];
			for (int k = 0; k < mList.size(); k++) {
				treeIds[k] = mList.get(k).getTree_id().toString();
			}
			Menu m = new Menu();
			m.setType(0);
			root = new DefaultTreeNode(m, null);
			addNode(0L, treeIds, root);
		}
	}

	private void addNode(Long parentId, String[] treeIds, TreeNode parent) {
		MenuDao d = (MenuDao) appContext.getContext().getBean("menuDao");
		String cond = String.format(" parent_id = %d AND tree_id IN(%s) ORDER BY sort_order ASC ", parentId,
				"'" + String.join("','", treeIds) + "'");
		List<Menu> list = d.findAll(cond);
		for (Menu m : list) {
			boolean isTransaction = false;
			if (!Validation.isEmptyLong(m.getTransaction_id())) {
				if (transactionMap.get(m.getTransaction_id()) != null) {
					m.setType(1);
					m.setLink(transactionMap.get(m.getTransaction_id()).getUrl());
					isTransaction = true;
				} else {
					// m.setType(0);
					continue;
				}
			} else {
				m.setType(0);
			}

			if (userData.getU_language().equals("ru")) {
				m.setName(m.getName_ru());
			} else if (userData.getU_language().equals("en")) {
				if (Validation.isEmptyString(m.getName_en())) {
					m.setName(m.getName_ru());
				} else {
					m.setName(m.getName_en());
				}
			} else if (userData.getU_language().equals("tr")) {
				if (Validation.isEmptyString(m.getName_tr())) {
					m.setName(m.getName_ru());
				} else {
					m.setName(m.getName_tr());
				}
			}
			TreeNode tn;
			if (isTransaction) {
				tn = new DefaultTreeNode("transaction", m, parent);
			} else {
				tn = new DefaultTreeNode(m, parent);
			}

			addNode(m.getId(), treeIds, tn);
		}
	}

	private Map<Long, Transaction> transactionMap = new HashMap<Long, Transaction>();
	private List<Transaction> transactionList;

	private void prepareTransactions() {
		transactionList = getTransactions();
		for (Transaction tr : transactionList) {
			transactionMap.put(tr.getTransaction_id(), tr);
		}
	}

	private List<Transaction> getTransactions() {
		TransactionDao tDao = (TransactionDao) appContext.getContext().getBean("transactionDao");
		if (userData.isSys_admin()) {
			return tDao.findAll("");
		} else {
			if (userData.getTransactionIds() != null) {
				String[] ids = new String[userData.getTransactionIds().length];
				int i = 0;
				for (Long trId : userData.getTransactionIds()) {
					ids[i] = trId.toString();
					i++;
				}

				if (ids.length == 0) {
					return new ArrayList<Transaction>();
				}
				String cond = String.format(" transaction_id IN(%s) ", "'" + String.join("','", ids) + "'");
				return tDao.findAll(cond);
			}

			return new ArrayList<Transaction>();
		}
	}

	public TreeNode getRoot() {
		return root;
	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public void onDragDrop(TreeDragDropEvent event) {
		TreeNode dragNode = event.getDragNode();

		TreeNode dropNode = event.getDropNode();
	}

	public void onNodeSelect(NodeSelectEvent event) {
		Menu selectedMenu = (Menu) (selectedNode.getData());
		if (selectedMenu.getType() == 1) {
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			Transaction transaction = transactionMap.get(selectedMenu.getTransaction_id());
			String url = context.getRequestContextPath() + selectedMenu.getLink().toString();
			if (transaction != null) {
				if (!Validation.isEmptyString(transaction.getFront_url())) {
					url = userData.getLinkToReact();
//					String serverName = context.getRequestServerName();
//					url = "http://192.168.0.15";
//
//					if ("werp.kz".equals(serverName)) {
//						url = "http://werp.kz";
//					}
//
//					if ("WERP".equals(userData.getConreqpath())) {
//						url += ":23040";
//					} else {
//						url += ":23041";
//					}

					url += (transaction.getFront_url().startsWith("/") ? "" : "/") + transaction.getFront_url();
				}
			}

			try {
				context.redirect(url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (this.selectedNode.isExpanded()) {
				this.selectedNode.setExpanded(false);
			} else {
				this.selectedNode.setExpanded(true);
			}
		}

		for (TreeNode tn : root.getChildren()) {
			if (tn != selectedNode && !this.inParents(tn) && !this.inChilds(tn)) {
				// System.out.println(selectedNode.getParent().getClass());
				tn.setExpanded(false);
				this.recursivelyCollsaps(tn.getChildren());
			}
		}
	}

	private boolean inParents(TreeNode tn) {
		if (tn.getParent() == selectedNode) {
			return true;
		}
		if (tn.getParent() == root) {
			return false;
		}
		return inParents(tn.getParent());
	}

	private boolean inChilds(TreeNode tn) {
		for (TreeNode tnLoc : tn.getChildren()) {
			if (tnLoc == selectedNode) {
				return true;
			}
		}

		boolean b = false;
		for (TreeNode tnLoc : tn.getChildren()) {
			if (tnLoc.getChildCount() > 0) {
				b = this.inChilds(tnLoc);
			}
			if (b) {
				return b;
			}
		}

		return false;
	}

	private void recursivelyCollsaps(List<TreeNode> tnList) {
		for (TreeNode tn : tnList) {
			tn.setExpanded(false);
			if (tn.getChildCount() > 0) {
				this.recursivelyCollsaps(tn.getChildren());
			}
		}
	}
}
