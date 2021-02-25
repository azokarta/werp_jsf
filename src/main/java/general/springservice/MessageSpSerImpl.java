package general.springservice;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dit.message.UserStaff;
import general.dao.DAOException;
import general.dao.UserDao;
import general.dao2.MessageAttachDao;
import general.dao2.MessageGroupDao;
import general.dao2.MessageGroupUserDao;
import general.dao2.MessageHeaderDao;
import general.dao2.MessageToDao;
import general.tables.User;
import general.tables2.MessageAttach;
import general.tables2.MessageGroup;
import general.tables2.MessageGroupUser;
import general.tables2.MessageHeader;
import general.tables2.MessageTo;

@Service("messageService")
public class MessageSpSerImpl implements MessageSpSer{
	@Autowired
	MessageGroupDao mgDao;
	
	@Autowired
	MessageGroupUserDao mguDao;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	MessageHeaderDao mhDao;
	
	@Autowired
	MessageToDao mtDao;
	
	@Autowired
	MessageAttachDao maDao;
	
	public void createMessageGroup(MessageGroup a_mg) throws DAOException{
		try
		{			
			if (a_mg!=null && a_mg.getGroup_name()!=null && a_mg.getGroup_name().length()>0)
			{
				mgDao.create(a_mg);				
			}
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	
	public void updateMessageGroup(MessageGroup a_mg) throws DAOException{
		try
		{			
			if (a_mg!=null && a_mg.getGroup_name()!=null && a_mg.getGroup_name().length()>0)
			{
				mgDao.update(a_mg);				
			}
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	public void deleteMessageGroup(MessageGroup a_mg) throws DAOException{
		try
		{			
			if (a_mg!=null && a_mg.getGroup_name()!=null && a_mg.getGroup_name().length()>0)
			{
				mgDao.delete(a_mg.getGroup_id());				
			}
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	public void createMessageGroupUser(MessageGroupUser a_mgu) throws DAOException{
		try
		{			
			if (a_mgu!=null && a_mgu.getGroup_id()!=null)
			{
				if (mguDao.findAll(" group_id = "+a_mgu.getGroup_id()+" and user_id="+a_mgu.getUser_id()).size()==0)
				{
					mguDao.create(a_mgu);
				}
								
			}
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	public void deleteMessageGroupUser(MessageGroupUser a_mgu) throws DAOException{
		try
		{			
			if (a_mgu!=null && a_mgu.getGroup_id()!=null)
			{
				mguDao.delete(a_mgu.getMgu_id());				
			}
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	
	public void sendMessage(List<UserStaff> al_us, MessageHeader a_mh,List<MessageAttach> al_ma, Long a_userId) throws DAOException{
		try
		{		
			Calendar curDate  = Calendar.getInstance();
//			List<MessageGroup> l_mg = new ArrayList<MessageGroup>();
			List<User> l_user_group = new ArrayList<User>();
//			List<User> l_user_only = new ArrayList<User>();
			boolean all = false;
			String whereMg = "";
			String wherePos = "";
			List<Long> ll_user_id = new ArrayList<Long>();
			for(UserStaff wa_us:al_us)
			{
				if (wa_us.getGroup_id()!=null && wa_us.getGroup_id()>0L)
				{
					if (whereMg.length()>0) whereMg = whereMg + "," + wa_us.getGroup_id();
					else whereMg = whereMg + wa_us.getGroup_id();
					
					if (wa_us.getGroup_id().equals(24L))
					{
						all = true;
					}
				}
				else if (wa_us.getPosition_id()!=null && wa_us.getPosition_id()>0L)
				{
					if (wherePos.length()>0) wherePos = wherePos + "," + wa_us.getGroup_id();
					else wherePos = wherePos + wa_us.getPosition_id();
				}
				else if (wa_us.getUser_id()!=null && wa_us.getUser_id()>0L)
				{
					ll_user_id.add(wa_us.getUser_id());
				}
				
				if (a_mh.getMess_to_names()==null)
					a_mh.setMess_to_names(wa_us.getUsername());
				else if (a_mh.getMess_to_names().length()>0) a_mh.setMess_to_names(a_mh.getMess_to_names()+","+wa_us.getUsername());
				
			}
			
			if (whereMg.length()>0)
			{
				whereMg = " and group_id in ("+whereMg+")  and u.is_active=1";
				l_user_group = userDao.findFromMessageGroup(whereMg);
				for(User wa_user:l_user_group)
				{
					ll_user_id.add(wa_user.getUser_id());
				}
			}
			
			
			
			if (wherePos.length()>0)
			{
				List<Long> l_pos_user = userDao.findFromPosition(wherePos);
				ll_user_id.addAll(l_pos_user);
			}
			
			if(all)
			{
				ll_user_id.clear();
				l_user_group = userDao.findAll("is_active=1");
				for(User wa_user:l_user_group)
				{
					ll_user_id.add(wa_user.getUser_id());
				}
				a_mh.setMess_to_names("All");
			}
			
			if (ll_user_id!=null && ll_user_id.size()>0)
			{
				Set<Long> hs = new HashSet<Long>();
				hs.addAll(ll_user_id);
				ll_user_id.clear();
				ll_user_id.addAll(hs);
				
				a_mh.setMess_date(curDate.getTime());
				a_mh.setMess_from(a_userId);
				
				
				mhDao.create(a_mh);
				for(Long a_user_id:ll_user_id)
				{
					MessageTo wa_mt = new MessageTo();
					wa_mt.setMess_id(a_mh.getMess_id());
					wa_mt.setMess_to(a_user_id);
					wa_mt.setRead_bool(1);
					mtDao.create(wa_mt);
				}
			}
			
			if (al_ma!=null && al_ma.size()>0)
			{
				for(MessageAttach wa_ma:al_ma)
				{
					wa_ma.setMess_id(a_mh.getMess_id());
					maDao.create(wa_ma);
				}
			}
			
				
				
				

			
			
			
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	
	public void markRead(List<Long> a_mess_to_id) throws DAOException{
		try
		{	
			mtDao.updateReadBool(a_mess_to_id, 0);
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	
	public void markUnread(List<Long> a_mess_to_id) throws DAOException{
		try
		{	
			mtDao.updateReadBool(a_mess_to_id, 1);
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	
	public void deleteMessage(List<Long> a_mess_to_id) throws DAOException{
		try
		{	
			for(Long wa:a_mess_to_id)
			{
				mtDao.delete(wa);
			}
			
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
}
