package general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.context.RequestContext;

public class Paginator<T>{
	
	public Paginator()
	{
		b_first_pn = true;
		b_prev_pn = true;
		b_next_pn = true;
		b_last_pn = true;
		b_refresh_pn = true;
		b_select_pn = true;
	};
	/** Member identifier for the current page number */
     private int currentPageNo = 1;


     /** Member identifier for the number of elements on a page */
     private int elementsPerPage = 20;

     /** Member identifier for the number of pages you have in the navigation (i.e 2 to  11 or 3 to 12 etc.) */      
     private int pageNumberInNavigation;
     
     private int enteredPageNo;
     
     
     int added =0;
     public void add(T a_out)
     {    	 
    	 l_all.add(a_out);
    	 added++;
    	 if (added==1)
    	 {
    		 List<T> wal_all = new ArrayList<T>();
    		 pageNumberInNavigation++;
    		 wal_all.add(a_out);
    		 l_map.put(Integer.parseInt(String.valueOf(pageNumberInNavigation)), wal_all);
    	 }
    	 else
    	 {
    		 List<T> wal_all = l_map.get(Integer.parseInt(String.valueOf(pageNumberInNavigation)));
    		 wal_all.add(a_out);
    	 }
    	 if (l_all.size()==1)
    	 {
    		 setFirstPage();
    	 }
    	 else if (l_all.size()==2)
    	 {
    		b_first_pn = false;
 			b_prev_pn = false;
 			b_next_pn = false;
 			b_last_pn = false;
 			b_refresh_pn = false;
 			b_select_pn = false;
    	 }
    	 if (added==elementsPerPage)
    	 {
    		 added = 0;	    		 
    	 }
     }

   List<T> l_all = new ArrayList<T>();
   Map<Integer,List<T>> l_map = new HashMap<Integer,List<T>>();
   private List<T> lc_all = new ArrayList<T>();
     public int getCurrentPageNo() {
		return currentPageNo;
	}

	public void setCurrentPageNo(int currentPageNo) {
		this.currentPageNo = currentPageNo;
		enteredPageNo = currentPageNo;
	}



	public List<T> getL_all() {
		return l_all;
	}

	public int getElementsPerPage() {
		return elementsPerPage;
	}

	public void setElementsPerPage(int elementsPerPage) {
		this.elementsPerPage = elementsPerPage;
	}

	public int getPageNumberInNavigation() {
		return pageNumberInNavigation;
	}

	public void setPageNumberInNavigation(int pageNumberInNavigation) {
		this.pageNumberInNavigation = pageNumberInNavigation;
	}



	public void setFirstPage()
	{
		if (pageNumberInNavigation>0)
		{
			lc_all = l_map.get(1);
			enteredPageNo = 1;
		}
		
		if (pageNumberInNavigation>1)
		{
			b_first_pn = false;
			b_prev_pn = false;
			b_next_pn = false;
			b_last_pn = false;
			b_refresh_pn = false;
			b_select_pn = false;
		}
			
		refresh();
	}

	public void first(){
         //Move to the previous page if exists
    	 if (getCurrentPageNo()==1)
    	 {
    		 return;
    	 }
         setCurrentPageNo(1);
         lc_all = l_map.get(1);
         refresh();
  			
     }
	public void last(){
         //Move to the next page if exists
    	 if (getCurrentPageNo()==pageNumberInNavigation)
    	 {
    		 return;
    	 }
         setCurrentPageNo(pageNumberInNavigation);
         lc_all = l_map.get(pageNumberInNavigation);
         refresh();
     } 

     public void next(){
         //Move to the next page if exists
    	 if (getCurrentPageNo()==pageNumberInNavigation)
    	 {
    		 return;
    	 }
    	 
         setCurrentPageNo(getCurrentPageNo() + 1);
         lc_all = l_map.get(getCurrentPageNo());
         refresh();
     }

     public void previous(){
         //Move to the previous page if exists
    	 if (getCurrentPageNo()==1)
    	 {
    		 return;
    	 }
    	 
         setCurrentPageNo(getCurrentPageNo() - 1);
         lc_all = l_map.get(getCurrentPageNo());
         refresh();
  			
     }

	public List<T> getLc_all() {
		return lc_all;
	}

	public void setLc_all(List<T> lc_all) {
		this.lc_all = lc_all;
	}
	
	public void selectPageNo()
	{
		if (enteredPageNo>pageNumberInNavigation)
		{
			enteredPageNo = pageNumberInNavigation;
		}
		else if (enteredPageNo<1)
		{
			enteredPageNo=1;
		}
		setCurrentPageNo(enteredPageNo);
        lc_all = l_map.get(enteredPageNo);
        refresh();
		
	}
	public void refresh(){
         //Your code here
		
		RequestContext reqCtx = RequestContext.getCurrentInstance();
  		reqCtx.update("form:outputTable");
  		reqCtx.update("form:customPaginator");
    }
	

	
	private boolean b_first_pn;
	private boolean b_prev_pn;
	private boolean b_next_pn;
	private boolean b_last_pn;
	private boolean b_refresh_pn;
	private boolean b_select_pn;

	public boolean isB_first_pn() {
		return b_first_pn;
	}

	public void setB_first_pn(boolean b_first_pn) {
		this.b_first_pn = b_first_pn;
	}

	public boolean isB_prev_pn() {
		return b_prev_pn;
	}

	public void setB_prev_pn(boolean b_prev_pn) {
		this.b_prev_pn = b_prev_pn;
	}

	

	public boolean isB_next_pn() {
		return b_next_pn;
	}

	public void setB_next_pn(boolean b_next_pn) {
		this.b_next_pn = b_next_pn;
	}

	public boolean isB_last_pn() {
		return b_last_pn;
	}

	public void setB_last_pn(boolean b_last_pn) {
		this.b_last_pn = b_last_pn;
	}

	public int getEnteredPageNo() {
		return enteredPageNo;
	}

	public void setEnteredPageNo(int enteredPageNo) {
		this.enteredPageNo = enteredPageNo;
	}

	public boolean isB_select_pn() {
		return b_select_pn;
	}

	public void setB_select_pn(boolean b_select_pn) {
		this.b_select_pn = b_select_pn;
	}

	public boolean isB_refresh_pn() {
		return b_refresh_pn;
	}

	public void setB_refresh_pn(boolean b_refresh_pn) {
		this.b_refresh_pn = b_refresh_pn;
	}
	
	
     
	
}