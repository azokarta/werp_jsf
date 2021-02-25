package general;


import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import user.ProgramManager;
import user.User;

public class SessionListener implements HttpSessionListener {
    private int sessionCount = 0;
 
    public void sessionCreated(HttpSessionEvent event) {
    	synchronized (this) {
            sessionCount++;
        }
    	/*
    	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        ProgramManager pm = (ProgramManager) request.getSession().getAttribute("programManagerBean");
        pm.removeSessionUser(event.getSession().getId());
        System.out.println("Total Sessions: " + pm.getL_user_actions().size());
        */
        //System.out.println("Session Destroyed: " + event.getSession().getId());
    	
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        HttpSession session = request.getSession(false);
        ProgramManager pm = (ProgramManager) event.getSession().getServletContext().getAttribute("programManagerBean");
        if ((session != null) && (session.getAttribute("user") != null))
        {
        	
        	System.out.println("Session Created: " + event.getSession().getId());
            System.out.println("Total Sessions: " + pm.getL_user_actions().size());
        }
        
    }
 
    public void sessionDestroyed(HttpSessionEvent event) {
        synchronized (this) {
            sessionCount--;
        }
        User user = (User) event.getSession().getAttribute("user");
        if (user != null) {
        	//ProgramManager programManager = (ProgramManager) event.getSession().getServletContext().getAttribute("programManagerBean");
            //loginManager.getUsers().remove(user);
            //programManager.removeSessionUser(event.getSession().getId());
            //System.out.println("Total Sessions: " + programManager.getL_user_actions().size());
            //System.out.println("Session Destroyed: " + event.getSession().getId());
        }
        
        //System.out.println("Total Sessions: " + sessionCount);
    }
}