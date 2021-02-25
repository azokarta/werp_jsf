package general;

import java.io.Serializable;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name = "systemLocale",eager = true)
@SessionScoped
public class SystemLocale implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6486132038872310420L;
	private Locale currentLocale = new Locale("ru");
	


    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(String localeCode) {
        localeCode = localeCode.toString();
        currentLocale = new Locale(localeCode);
    }

    public String getCurrentLanguage() {
        return currentLocale.getLanguage();
    }

    public void setCurrentLanguage(String lang) {
        setCurrentLocale(lang);
    }

    @ManagedProperty(value = "#{userinfo}")
    private user.User userData;

    public user.User getUserData() {
        return userData;
    }

    public void setUserData(user.User userData) {
        this.userData = userData;
    }
}
