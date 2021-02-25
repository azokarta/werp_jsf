package dit.message;


import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.PositionDao;
import general.dao.UserDao;
import general.dao2.MessageAttachDao;
import general.dao2.MessageGroupDao;
import general.dao2.MessageHeaderDao;
import general.springservice.MessageSpSer;
import general.tables.Position;
import general.tables2.MessageAttach;
import general.tables2.MessageGroup;
import general.tables2.MessageHeader;
import java.io.InputStream;
import java.sql.SQLException;
import java.io.IOException;
import org.apache.poi.util.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import user.User;


@ManagedBean(name = "messageBean", eager = true)
@ViewScoped
public class MessageBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	//private final static Long read = (long) 1;
	//private final static Long write = (long) 2; 
		
	//***************************Application Context********************
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
		return appContext;
	}
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	//******************************************************************
	//**********************Getter Setter for sessionData**********************
	@ManagedProperty(value="#{userinfo}")
	private User userData;
	public User getUserData() {
		return userData;
	}
	public void setUserData(User userData) {
		this.userData = userData;
	}
	
	private String pageName = "";
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	@PostConstruct
	public void init() {
		// TODO PERMISSION
		if (GeneralUtil.isAjaxRequest()) {
			return;
		}
		
		
		loadMessIn();
		
		int count = 0;
		MessageGroupDao d = (MessageGroupDao) appContext.getContext().getBean("messageGroupDao");
		l_mg = d.findAll("");
		for (MessageGroup wa_mg: l_mg)
		{
			count++;
			UserStaff wa_out = new UserStaff();
			wa_out.setGroup_id(wa_mg.getGroup_id());
			wa_out.setUsername(wa_mg.getGroup_name());
			wa_out.setFilteredname(wa_mg.getGroup_name());
			wa_out.setId(count);
			l_userstaff.add(wa_out);
		}
		
		UserDao ud = (UserDao) appContext.getContext().getBean("userDao");
		List<Object[]> results = new ArrayList<Object[]>();
		results = ud.getUsernameFio(" and u.is_active=1");
		for (Object[] wa_result:results)
		{
			count++;
			//a2.text45 city,a2.waers,a2.hkont,a2.usd,a2.kzt,a2.uzs,a2.kgs,a2.azn,a2.rn,s2.text45 hkontname
			UserStaff wa_out = new UserStaff();
			if (wa_result[0]!=null) wa_out.setUser_id(Long.valueOf(String.valueOf(wa_result[0])));
			
			if (wa_result[1]!=null){ 
				wa_out.setUsername(String.valueOf(wa_result[1]));
			}
			if (wa_result[2]!=null){
				wa_out.setUserFio(String.valueOf(wa_result[2]));
			}
			wa_out.setFilteredname(wa_out.getUsername()+" "+wa_out.getUserFio());
			wa_out.setId(count);
			l_userstaff.add(wa_out);			
		}
		
		
		PositionDao pd = (PositionDao) appContext.getContext().getBean("positionDao");
		List<Position> l_position = new ArrayList<Position>();
		l_position = pd.findAll();
		for (Position wa: l_position)
		{
			count++;
			UserStaff wa_out = new UserStaff();
			wa_out.setPosition_id(wa.getPosition_id());
			wa_out.setUsername(wa.getText());
			wa_out.setFilteredname(wa.getText());
			wa_out.setId(count);
			l_userstaff.add(wa_out);
		}
	}
	//******************************************************************
	//******************************************************************
	
	private int activeMess = 1;
	
	public int getActiveMess() {
		return activeMess;
	}
	public void setActiveMess(int activeMess) {
		this.activeMess = activeMess;
	}
	
	private int countNotRead = 0;
	
	public int getCountNotRead() {
		return countNotRead;
	}
	public void setCountNotRead(int countNotRead) {
		this.countNotRead = countNotRead;
	}
	
	private String notReadMessNum="";
	
	
	public String getNotReadMessNum() {
		return notReadMessNum;
	}
	public void setNotReadMessNum(String notReadMessNum) {
		this.notReadMessNum = notReadMessNum;
	}
	public void loadMessIn() {
		pageName = "Входящие";
		l_mess.clear();
		countNotRead=0;
		MessageHeaderDao messageHeaderDao = (MessageHeaderDao) appContext.getContext().getBean("messageHeaderDao");
		List<Object[]> results = new ArrayList<Object[]>();
		results = messageHeaderDao.findInMessage(" mt.mess_to="+userData.getUserid());
		
//		mh.mess_date, "
//				+"initcap(s.lastname) || ' ' || initcap(s.firstname) || ' ' || initcap(s.middlename) "
//				+",mh.mess_head_text "
//			       +",mh.mess_id "
//			       +",mt.mess_to "
//			       +",mt.message_to_id "
//			       +",mt.read_bool "
		
		
		for (Object[] result : results) {
			Message rt = new Message();
			
			
			if (result[0]!=null) rt.setMess_date(String.valueOf(result[0]));
			if (result[1]!=null) rt.setMess_from(String.valueOf(result[1]));
			if (result[2]!=null) rt.setMess_head_text(String.valueOf(result[2]));
			if (result[3]!=null) rt.setMess_id(Long.parseLong(String.valueOf(result[3])));
			if (result[5]!=null) rt.setMessage_to_id(Long.parseLong(String.valueOf(result[5])));
			if (result[6]!=null) rt.setRead_bool(Integer.parseInt(String.valueOf(result[6])));
			l_mess.add(rt);
			if (rt.read_bool==1)
			{
				countNotRead++;
			}
    	}
		if (countNotRead>0)
		{
			notReadMessNum = "("+countNotRead+")";
		}else notReadMessNum="";
		

		activeMess=1;
	}
	public void loadMessTo() {
//		// System.out.println("LOADING...");
//		MessageGroupUserDao d = (MessageGroupUserDao) appContext.getContext().getBean("messageGroupUserDao");
//		items = d.findAll(searchModel.getCondition());
//		selected = new MessageGroupUser();
		l_mess.clear();
		pageName = "Отправленные";
		MessageHeaderDao messageHeaderDao = (MessageHeaderDao) appContext.getContext().getBean("messageHeaderDao");
		List<Object[]> results = new ArrayList<Object[]>();
		results = messageHeaderDao.findToMessage(" mh.mess_from="+userData.getUserid());
		
		int count = 0;
		for (Object[] result : results) {
			count++;
			Message rt = new Message();
			
//			CASE WHEN TO_CHAR (SYSDATE, 'MM-DD-YYYY') = to_char(mh.mess_date, 'MM-DD-YYYY') then to_char(mh.mess_date, 'HH24:MI') else  to_char(mh.mess_date, 'DD.MM.YYYY') end, "
//					+"mh.mess_to_names "
//					+",mh.mess_head_text "
			if (result[0]!=null) rt.setMess_date(String.valueOf(result[0]));
			if (result[1]!=null) rt.setMess_to(String.valueOf(result[1]));
			if (result[2]!=null) rt.setMess_head_text(String.valueOf(result[2]));
			if (result[3]!=null) rt.setMess_id(Long.parseLong(String.valueOf(result[3])));
			rt.setMessage_to_id(Long.parseLong(String.valueOf(count)));
			l_mess.add(rt);
    	}
		

		activeMess=2;
//		System.out.println("ZZZZZZZ");
	}
	public void writeMessage() {
		pageName = "Написать";
		if (selected_userstaff!=null && selected_userstaff.size()>0)
		{
			selected_userstaff.clear();
		}
		new_message=new MessageHeader();
		l_ma = new ArrayList<MessageAttach>();
		
//		System.out.println("Write message");
		activeMess=3;
	}
	
	
	public void readMessage()
	{
		if (selected_mess!=null)
		{
			selected_mess.clear();			
		}
		
		
		selected_mess.add(read_message);
		markRead();
		
		l_ma = new ArrayList<MessageAttach>();
		MessageAttachDao messageAttachDao = (MessageAttachDao) appContext.getContext().getBean("messageAttachDao");
		l_ma = messageAttachDao.findAll("mess_id ="+read_message.getMess_id());
		
		MessageHeaderDao messageHeaderDao = (MessageHeaderDao) appContext.getContext().getBean("messageHeaderDao");
		read_message.setMess_text(messageHeaderDao.find(read_message.getMess_id()).getMess_text());
		if (activeMess==1) activeMess=4;
		else activeMess=5;
		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form");
	}
	
	public void markRead()
	{
		List<Long> l_mess_to_id = new ArrayList<Long>();
		for(Message wa:selected_mess)
		{
			if (wa.getRead_bool()==1)
			{
				l_mess_to_id.add(wa.getMessage_to_id());
			}
		}
		if (l_mess_to_id.size()>0)
		{
			MessageSpSer messageService = (MessageSpSer) appContext.getContext().getBean("messageService");
			messageService.markRead(l_mess_to_id);
			for(Message wa:selected_mess)
			{
				if (wa.getRead_bool()==1)
				{
					wa.setRead_bool(0);
					countNotRead--;
				}
			}
			if (countNotRead>0)
			{
				notReadMessNum = "("+countNotRead+")";
			}else notReadMessNum="";
				
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:menu");
		}
		
	}
	
	public void markUnread()
	{
		List<Long> l_mess_to_id = new ArrayList<Long>();
		for(Message wa:selected_mess)
		{
			if (wa.getRead_bool()==0)
			{
				l_mess_to_id.add(wa.getMessage_to_id());
			}
		}
		if (l_mess_to_id.size()>0)
		{
			MessageSpSer messageService = (MessageSpSer) appContext.getContext().getBean("messageService");
			messageService.markUnread(l_mess_to_id);
			for(Message wa:selected_mess)
			{
				if (wa.getRead_bool()==0)
				{
					wa.setRead_bool(1);
					countNotRead++;
				}
			}
			if (countNotRead>0)
			{
				notReadMessNum = "("+countNotRead+")";
			}
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:menu");
		}
		
	}
	
	
	//******************************************************************



	//******************************************************************
	//******************************************************************
	public void sendMessage() {
		try
		{
			
			if (selected_userstaff!=null && selected_userstaff.size()>0 && new_message.getMess_head_text()!=null && new_message.getMess_head_text().length()>0)
			{
				MessageSpSer messageService = (MessageSpSer) appContext.getContext().getBean("messageService");
				messageService.sendMessage(selected_userstaff, new_message,l_ma,userData.getUserid());
				
			
			}
			selected_userstaff.clear();
			new_message = new MessageHeader();
			loadMessIn();
			loadMessTo();
			RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form");

			
		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
		
	}
	public void deleteMessage() {
		try
		{
			List<Long> l_mess_to_id = new ArrayList<Long>();
			for(Message wa:selected_mess)
			{
				l_mess_to_id.add(wa.getMessage_to_id());
			}
			if (l_mess_to_id.size()>0)
			{
				MessageSpSer messageService = (MessageSpSer) appContext.getContext().getBean("messageService");
				messageService.deleteMessage(l_mess_to_id);
				loadMessIn();
				RequestContext reqCtx = RequestContext.getCurrentInstance();
				reqCtx.update("form");
			}

		}
		catch (DAOException ex)
		{
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
		
	}
	// *****************************************************************************	

	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:messages");
    }
	List<Message> l_mess = new ArrayList<Message>();	
	List<Message> selected_mess = new ArrayList<Message>();
	

	public List<Message> getL_mess() {
		return l_mess;
	}
	public void setL_mess(List<Message> l_mess) {
		this.l_mess = l_mess;
	}
	public List<Message> getSelected_mess() {
		return selected_mess;
	}
	public void setSelected_mess(List<Message> selected_mess) {
		this.selected_mess = selected_mess;
	}

	public class Message {
		private Long message_to_id;
		private Long mess_id;
		private String mess_from;
		private String mess_to;
		private String mess_head_text;
		private String mess_date;
		private int read_bool;
		private String mess_text;
		public Long getMessage_to_id() {
			return message_to_id;
		}
		public void setMessage_to_id(Long message_to_id) {
			this.message_to_id = message_to_id;
		}
		public Long getMess_id() {
			return mess_id;
		}
		public void setMess_id(Long mess_id) {
			this.mess_id = mess_id;
		}
		public String getMess_from() {
			return mess_from;
		}
		public void setMess_from(String mess_from) {
			this.mess_from = mess_from;
		}
		public String getMess_head_text() {
			return mess_head_text;
		}
		public void setMess_head_text(String mess_head_text) {
			this.mess_head_text = mess_head_text;
		}
		public String getMess_date() {
			return mess_date;
		}
		public void setMess_date(String mess_date) {
			this.mess_date = mess_date;
		}
		public int getRead_bool() {
			return read_bool;
		}
		public void setRead_bool(int read_bool) {
			this.read_bool = read_bool;
		}
		public String getMess_to() {
			return mess_to;
		}
		public void setMess_to(String mess_to) {
			this.mess_to = mess_to;
		}
		public String getMess_text() {
			return mess_text;
		}
		public void setMess_text(String mess_text) {
			this.mess_text = mess_text;
		}
		
	}





	
	List<MessageGroup> l_mg = new ArrayList<MessageGroup>();	
	public List<MessageGroup> getL_mg() {
		return l_mg;
	}
	public void setL_mg(List<MessageGroup> l_mg) {
		this.l_mg = l_mg;
	}
	
	List<UserStaff> l_userstaff = new ArrayList<UserStaff>();
	public List<UserStaff> getL_userstaff() {
		return l_userstaff;
	}
	public void setL_userstaff(List<UserStaff> l_userstaff) {
		this.l_userstaff = l_userstaff;
	}
	List<UserStaff> selected_userstaff = new ArrayList<UserStaff>();
	
	
	public List<UserStaff> getSelected_userstaff() {
		return selected_userstaff;
	}
	public void setSelected_userstaff(List<UserStaff> selected_userstaff) {
		this.selected_userstaff = selected_userstaff;
	}
	public List<UserStaff> autocompleteUS(String a_text){
        List<UserStaff> filteredLus = new ArrayList<UserStaff>();
         
        for (UserStaff wa: l_userstaff) {
        	
        	if (wa.getFilteredname().toLowerCase().contains(a_text.toLowerCase()))
        	{
        		filteredLus.add(wa);
        	}
        }
//		System.out.println(a_text);
		return filteredLus;
	}
	
	 
	//*****************************************************************************************************
	private MessageHeader new_message = new MessageHeader();	
	public MessageHeader getNew_message() {
		return new_message;
	}
	public void setNew_message(MessageHeader new_message) {
		this.new_message = new_message;
	}
	
	
	private Message read_message = new Message();	
	public Message getRead_message() {
		return read_message;
	}
	public void setRead_message(Message read_message) {
		this.read_message = read_message;
	}
	
	public List<String> getSelected_users() {
		return selected_users;
	}
	public void setSelected_users(List<String> selected_users) {
		this.selected_users = selected_users;
	}

	private List<String> selected_users = new ArrayList<String>();
	
	private MessageToConverter  messageToConverter = new MessageToConverter();
	
	
	public MessageToConverter getMessageToConverter() {
		return messageToConverter;
	}
	public void setMessageToConverter(MessageToConverter messageToConverter) {
		this.messageToConverter = messageToConverter;
	}

	public class MessageToConverter implements Converter {
		@Override
		public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	        if(value != null && value.trim().length() > 0) {
	            try {
	            	;
//	            	UserStaff wa = new UserStaff();
//	            	wa.setUsername("Z1");
//	            	System.out.println(value);		
	                return l_userstaff.get(Integer.parseInt(value)-1);//l_userstaff.get(Integer.parseInt(value));
	            } catch(NumberFormatException e) {
	                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
	            }
	        }
	        else {
	            return null;
	        }
	    }
	 
	    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
	        if(object != null) {
	        	String value = "";
	        	value =  String.valueOf(((UserStaff) object).getId());
//	        	if (((UserStaff) object).getGroup_id()!=null && ((UserStaff) object).getGroup_id()>0L)
//	        	{
//	        		value = "G"+String.valueOf(((UserStaff) object).getGroup_id());
//	        	}
	            return value;
	        }
	        else {
	            return null;
	        }
	    }

	 
	}
	
	//File Handle
	private List<MessageAttach> l_ma = new ArrayList<MessageAttach>();
	
	
	
	public List<MessageAttach> getL_ma() {
		return l_ma;
	}
	public void setL_ma(List<MessageAttach> l_ma) {
		this.l_ma = l_ma;
	}
	public void handleFileUpload(FileUploadEvent event) throws IOException, SerialException, SQLException {
		if (l_ma.size()<3)
		{
			MessageAttach wa_ma = new MessageAttach();
			InputStream a_in = event.getFile().getInputstream();
			byte[] bytes = IOUtils.toByteArray(a_in);
			wa_ma.setMess_attach(new SerialBlob(bytes));
			wa_ma.setExt(event.getFile().getFileName());
			System.out.println(wa_ma.getExt().length());
			if (wa_ma.getExt().length()>50)
			{
				wa_ma.setExt("temp."+wa_ma.getExt().substring(wa_ma.getExt().lastIndexOf(".")+1));
			}
//			wa_ma.setMess_id(a_mh.getMess_id());
			
			l_ma.add(wa_ma);
			
			FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
	        FacesContext.getCurrentInstance().addMessage(null, message);
	        RequestContext reqCtx = RequestContext.getCurrentInstance();
			reqCtx.update("form:attachTable");
		}
		
		

        
    }
	public void removeFile(String a_name) {
		List<MessageAttach> l_ma2 = new ArrayList<MessageAttach>();
		
		for(MessageAttach wa:l_ma)
		{
			if (!a_name.equals(wa.getExt()))
			{
				l_ma2.add(wa);
			}
		}
		l_ma = new ArrayList<MessageAttach>();
		l_ma.addAll(l_ma2);
//		l_file.get(0).getSize()
//		InputStream a_in = u_file.getInputstream();
//		byte[] bytes = IOUtils.toByteArray(a_in);
//		wa_zr.setFileu(new SerialBlob(bytes));
//		wa_zr.setName(u_file.getFileName());
		
        RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:attachTable");
        
    }
	
	public StreamedContent download(String a_filename) 
	{
        try {
            
        	for(MessageAttach wa:l_ma)
    		{
    			if (a_filename.equals(wa.getExt()))
    			{
    				FacesContext fc = FacesContext.getCurrentInstance();  
    				HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
    			     
    				
    			    String userAgent = request.getHeader("user-agent");
    			    boolean isInternetExplorer = (userAgent.indexOf("MSIE") > -1);		     
    			    byte[] fileNameBytes = a_filename.getBytes((isInternetExplorer) ? ("windows-1250") : ("utf-8"));
    			    String dispositionFileName = "";
    			    for (byte b: fileNameBytes) dispositionFileName += (char)(b & 0xff);
    				
    				InputStream in = wa.getMess_attach().getBinaryStream();
    		        StreamedContent file = new DefaultStreamedContent(in, "image/jpg", dispositionFileName);
    		            
    		        return file;
    			}
    		}
           


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		return null;
    
    
	}
}
