package general;

import general.dao.DAOException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

public class GeneralUtil {

	public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, (new MessageProvider()).getValue("error").toUpperCase(), msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addSuccessMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, (new MessageProvider()).getValue("success").toUpperCase(), msg);
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }
    
    public static void addInfoMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", msg);
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }

    public static String getRequestParameter(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
    }
    
    public static void hideDialog(String widgetVar){
    	RequestContext.getCurrentInstance().execute(
				"PF('" + widgetVar + "').hide();");
    }
    
    public static void showDialog(String widgetVar){
    	RequestContext.getCurrentInstance().execute(
				"PF('" + widgetVar + "').show();");
    }
    
    public static void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
    
    public static void doRedirect(String url) {
    	if(!url.startsWith("/")){
    		url = "/" + url;
    	}
		try {
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			context.redirect(context.getRequestContextPath() + url);
		} catch (Exception ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}
    
    public static boolean isAjaxRequest(){
    	return FacesContext.getCurrentInstance().getPartialViewContext()
		.isAjaxRequest();
    }
    public static Calendar getDate(int a_day, int a_month, int a_gjahr)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(a_gjahr, a_month-1, a_day);
    	return cal;
    }
    public static java.util.Date removeTime(java.util.Date a_date)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(a_date);    	
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    	
    	
    	return cal.getTime();
    }
    
    public static Calendar getLastDayOfMonth(Calendar a_cal)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(a_cal.getTime());
    	cal.set(Calendar.HOUR_OF_DAY, 23);
    	cal.set(Calendar.MINUTE, 59);
    	cal.set(Calendar.SECOND, 59);
    	cal.set(Calendar.DAY_OF_MONTH, a_cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    	return cal;
    }
    public static java.sql.Date getSQLDate(Calendar a_cal)
    {
    	return new java.sql.Date(a_cal.getTimeInMillis());
    }
    public static java.sql.Date getSQLDate(java.util.Date a_date)
    {
    	removeTime(a_date);
    	Calendar wa_cal = Calendar.getInstance();
    	wa_cal.setTime(a_date);    	
    	return new java.sql.Date(wa_cal.getTimeInMillis());
    }
    
    public static void checkForNullLong(Long a_long_value, String a_var_name)
    {
    	try{
    		if (a_long_value==null)
    		{
    			throw new DAOException("The value of "+a_var_name + " is null ");
    		}
    	}
    	catch (DAOException ex)
    	{
    		throw new DAOException(ex.getMessage());
    	}
    }
    public static void checkForNullLong(String a_string_value, String a_var_name)
    {
    	try{
    		if (a_string_value==null)
    		{
    			throw new DAOException("The value of "+a_var_name + " is null ");
    		}
    		else if (a_string_value!=null &&a_string_value.length()<1)
    		{
    			throw new DAOException("The value of "+a_var_name + " is null ");
    		}
    	}
    	catch (DAOException ex)
    	{
    		throw new DAOException(ex.getMessage());
    	}
    }
    public static Calendar getLastDayOfMonth(int a_year, int a_month)
    {
    	Calendar date_to = Calendar.getInstance();
    	date_to.set(Calendar.YEAR, a_year);
		date_to.set(Calendar.MONTH, a_month-1);
		date_to.set(Calendar.DAY_OF_MONTH, date_to.getActualMaximum(Calendar.DAY_OF_MONTH));
			
    	return date_to;
    }
    public static Calendar getCurrentMonthLastDay()
    {
    	Calendar curDate = Calendar.getInstance();
		Calendar calLast = Calendar.getInstance();
		calLast = GeneralUtil.getLastDayOfMonth(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH)+1);			
    	return calLast;
    }
    public static Calendar getCurrentMonthFirstDay()
    {
    	Calendar curDate = Calendar.getInstance();
		Calendar calFirst = Calendar.getInstance();
		calFirst.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 1);		
    	return calFirst;
    }
    public static void checkForNull0(Long a_long_value, String a_var_name)
    {
    	try{
    		if (a_long_value==0)
    		{
    			throw new DAOException("The value of "+a_var_name + " is 0 ");
    		}
    	}
    	catch (DAOException ex)
    	{
    		throw new DAOException(ex.getMessage());
    	}
    }
    
    public static Long getPreparedAwkey(Long belnr, int gjahr){
    	return Long.parseLong(String.valueOf(belnr) + String.valueOf(gjahr));
    }
    
    public static Long getPreparedBelnr(Long awkey){
    	if (awkey!=null && awkey>0)
    	{
    		String awkey_str="";
    		awkey_str = String.valueOf(awkey);
    		if (awkey_str.length()==14)
    		{
    			return Long.parseLong(awkey_str.substring(0, 10));
    		}
    		else
    		{
    			return null;
    		}
    		
    	}
    	else
    	{
    		return null;
    	}
    }
    public static int getPreparedGjahr(Long awkey){
    	if (awkey!=null && awkey>0)
    	{
    		String awkey_str="";
    		awkey_str = String.valueOf(awkey);
    		if (awkey_str.length()==14)
    		{
    			return Integer.parseInt(awkey_str.substring(10, 14));
    		}
    		else
    		{
    			return 0;
    		}
    		
    		
    	}
    	else
    	{
    		return 0;
    	}
    	
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static void updateFormElement(String elemId){
    	RequestContext.getCurrentInstance().update(elemId);
    }
    
    // *************************************************************************
    public static Date getFirstDateOfMonth(Date in_d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(in_d);
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getLastDateOfMonth(Date in_d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(in_d);
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		return cal.getTime();
	}
	
    public static int calcAgeinMonths(Date d1, Date d2)
	   {
	      int years = 0;
	      int months = 0;
	      Calendar cd2 = Calendar.getInstance();
	      cd2.setTime(d2);
	      Calendar cd1 = Calendar.getInstance();
	      cd1.setTime(d1);
	      years = cd1.get(Calendar.YEAR) - cd2.get(Calendar.YEAR);
	      int mon1 = cd1.get(Calendar.MONTH) + 1;
	      int mon2 = cd2.get(Calendar.MONTH) + 1;
	      months = mon1 - mon2 + (years * 12);
	      return months;
	   }
}