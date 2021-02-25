package general;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean; 

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@ManagedBean(name = "appContext", eager = true)
@ApplicationScoped
public class AppContext implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
	
	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}
	
	private Long hrplUserId;
	public Long getHrplUserId() {
		return hrplUserId;
	}
	public void setHrplUserId(Long hrplUserId) {
		this.hrplUserId = hrplUserId;
	}

	
	
}
