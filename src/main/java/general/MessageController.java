package general;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessageController {

	private static MessageController instance;
	private MessageController(){}
	
	public static MessageController getInstance() {
		if(instance == null)
		{
			instance = new MessageController();
		}
		return instance;
	}
	
	public void addInfo(String msg)
	{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", msg));
	}
	
	public void addError(String msg)
	{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg));
	}
}