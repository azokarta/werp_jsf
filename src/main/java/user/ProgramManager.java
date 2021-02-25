package user;

import general.dao.DAOException;
import general.tables.Bkpf;
import general.tables.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
@ManagedBean(name = "programManagerBean", eager = true)
@ApplicationScoped
public class ProgramManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<UserActions> l_user_actions = new ArrayList<UserActions>();	
	public List<UserActions> getL_user_actions() {
		return l_user_actions;
	}

	
	public class UserActions{
		public UserActions()
		{
			
		}
		String session_id = "";
		int maxTabId = 0;
		private user.User user = new user.User();
		List<TransactionWithDocuments> l_trans = new ArrayList<TransactionWithDocuments>();
		public String getSession_id() {
			return session_id;
		}
		public void setSession_id(String session_id) {
			this.session_id = session_id;
		}
		public user.User getUser() {
			return user;
		}
		public void setUser(user.User user) {
			this.user = user;
		}
		public List<TransactionWithDocuments> getL_trans() {
			return l_trans;
		}
		public void setL_trans(List<TransactionWithDocuments> l_trans) {
			this.l_trans = l_trans;
		}				
		public int getMaxTabId() {
			return maxTabId;
		}
		public void setMaxTabId(int maxTabId) {
			this.maxTabId = maxTabId;
		}		
	}
	public class TransactionWithDocuments{
		public TransactionWithDocuments()
		{
			
		}
		private int tabID = 0;
		Transaction transaction = new Transaction();
		List<Bkpf> l_bkpf = new ArrayList<Bkpf>();
		public Transaction getTransaction() {
			return transaction;
		}
		public void setTransaction(Transaction transaction) {
			this.transaction = transaction;
		}
		public List<Bkpf> getL_bkpf() {
			return l_bkpf;
		}
		public void setL_bkpf(List<Bkpf> l_bkpf) {
			this.l_bkpf = l_bkpf;
		}
		public int getTabID() {
			return tabID;
		}
		public void setTabID(int tabID) {
			this.tabID = tabID;
		}
		
		
	}
	public void addNewSessionUser(User a_user, String a_sessionId)
	{
		try
		{
			for(UserActions wa_ua:l_user_actions)
			{
				System.out.println(wa_ua.session_id);
			}
			
			UserActions ua = new UserActions();
			ua.setSession_id(a_sessionId);
			ua.setUser(a_user);
			l_user_actions.add(ua);
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void removeSessionUser(String a_sessionId)
	{
		try
		{
			List<Integer> l_indexes = new ArrayList<Integer>();
			int index = 0;
			for(UserActions wa_ua:l_user_actions)
			{
				if (wa_ua.getSession_id().equals(a_sessionId))
				{
					l_indexes.add(index);
				}
				index++;
			}
			if (l_indexes.size()>0)
			{	
				for(Integer wa_index:l_indexes)
				{
					l_user_actions.remove(l_user_actions.get(wa_index));
				}
			}
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
}
