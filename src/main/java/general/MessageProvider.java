package general;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class MessageProvider {

	private ResourceBundle bundle = null;
	
	public ResourceBundle getBundle(){
		if(null == this.bundle){
			FacesContext fc = FacesContext.getCurrentInstance();
			this.bundle = fc.getApplication().getResourceBundle(fc, "msg");
		}
		return this.bundle;
	}
	
	public String getValue(String key){
		String result = null;
        try {
            result = getBundle().getString(key);
        } catch (MissingResourceException e) {
            result = "???" + key + "??? not found";
        }
        return result;
	}
	
	public String getErrorValue(String key){
		key = "error." + key;
		return this.getValue(key);
	}
	
	public String getSuccessValue(String k){
		k = "success." + k;
		return this.getValue(k);
	}
}
