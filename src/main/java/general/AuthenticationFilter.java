package general;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.faces.application.ViewExpiredException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.User;

public class AuthenticationFilter implements Filter {
	private static final String AJAX_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	        + "<partial-response><redirect url=\"%s\"></redirect></partial-response>";
	/**
	 * Checks if user is logged in. If not it redirects to the login.xhtml page.
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
	        HttpServletResponse response = (HttpServletResponse) res;
	        HttpSession session = request.getSession(false);
	        String loginURL = request.getContextPath() + "/index.xhtml";
	        String loginURL2 = request.getContextPath() + "/";
	        //User session = (User) request.getSession().getAttribute("userinfo");
	        boolean loggedIn = (session != null) && (session.getAttribute("user") != null);
	        //boolean loggedIn = (session != null && session.isLogged_in());
	        boolean loginRequest = request.getRequestURI().equals(loginURL);
	        boolean loginRequest2 = request.getRequestURI().equals(loginURL2);
	        boolean resourceRequest = request.getRequestURI().startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER + "/");
	        boolean ajaxRequest = "partial/ajax".equals(request.getHeader("Faces-Request"));	        
	        
	        if (loggedIn && (loginRequest || loginRequest2)) {
	            if (!resourceRequest) { // Prevent browser from caching restricted resources. See also http://stackoverflow.com/q/4194207/157882
	                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
	                response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	                response.setDateHeader("Expires", 0); // Proxies.
	            }
	            response.sendRedirect(request.getContextPath() + "/general/mainPage.xhtml");
	        }
	        else if (loggedIn || loginRequest || resourceRequest) {
	            if (!resourceRequest) { // Prevent browser from caching restricted resources. See also http://stackoverflow.com/q/4194207/157882
	                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
	                response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	                response.setDateHeader("Expires", 0); // Proxies.
	            }
	            
	            chain.doFilter(request, response); // So, just continue request.
	        }
	        else if (ajaxRequest) {
	            response.setContentType("text/xml");
	            response.setCharacterEncoding("UTF-8");
	            response.getWriter().printf(AJAX_REDIRECT_XML, loginURL); // So, return special XML response instructing JSF ajax to send a redirect.
	        }
	        else {
	            response.sendRedirect(loginURL); // So, just perform standard synchronous redirect.
	        }
/*
			// Get the loginBean from session attribute
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			User session = (User) req.getSession().getAttribute("userinfo");
			String url = req.getRequestURI();
			String contextPath = req.getContextPath();
			int lengthOfUrl = url.length();

			if (!req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) { // Skip // JSF // resources
												 
				res.setHeader("Cache-Control", "no-cache, no-store"); // HTTP // 1.1.
				res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
				res.setDateHeader("Expires", -1); // Proxies.
				//System.out.println(session.isLogged_in());
				if (session == null || !session.isLogged_in()) 
				{ //
					System.out.println(111);
					if ((url.indexOf("index.xhtml") >= 0 || (lengthOfUrl == 8))) {
						System.out.println(222);
						chain.doFilter(request, response); 
						
						if (session==null || !session.isLogged_in())
						{
							//res.sendRedirect(contextPath + "/index.xhtml"); 
						}
						else
						{
							//res.sendRedirect(contextPath + "/general/mainPage.xhtml");
						}
						
					} else { 
						System.out.println(333);
						res.sendRedirect(contextPath + "/index.xhtml"); 
					}
				} 
				else if (session.isLogged_in()) {
					if ((url.indexOf("index.xhtml") >= 0) || (lengthOfUrl == 8)) { 
						res.sendRedirect(contextPath + "/general/mainPage.xhtml"); 
					}
					else { 
						chain.doFilter(request, response); 
					}
				}

			}
*/
		} catch (ServletException e) { 
			if (e.getRootCause() instanceof ViewExpiredException) {
				HttpServletRequest request  = (HttpServletRequest) req;
				HttpServletResponse response  = (HttpServletResponse) res;
				String contextPath = request.getContextPath();
				response.sendRedirect(contextPath + "/general/mainPage.xhtml");
			} else {
				throw e;
			}
		}

	}

	public void init(FilterConfig config) throws ServletException {

	}

	public void destroy() {
		// Nothing to do here!
	}

}
