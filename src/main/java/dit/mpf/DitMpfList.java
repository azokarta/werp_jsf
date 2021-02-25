package dit.mpf;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.MainPageFoldersDao;
import general.services.MainPageFoldersService;
import general.tables.MainPageFolders;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="ditMpfListBean")
@ViewScoped
public class DitMpfList implements Serializable{


	private static final long serialVersionUID = 1L;
	
	List<MainPageFolders> items;
	private MainPageFolders selected;
	
	
	public MainPageFolders getSelected() {
		return selected;
	}

	public void setSelected(MainPageFolders selected) {
		this.selected = selected;
	}

	public List<MainPageFolders> getItems() {
		MainPageFoldersDao d = (MainPageFoldersDao)appContext.getContext().getBean("mainPageFoldersDao");
		this.items = d.findAll("");
		return items;
	}

	public void Create(){
		try{
			if(this.selected == null){
				throw new DAOException("Ошибка сервера");
			}
			MainPageFoldersService service = (MainPageFoldersService)appContext.getContext().getBean("mainPageFoldersService");
			service.create(this.selected);
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.hideDialog("MpfCreateDialog");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void Update(){
		try{
			if(this.selected == null){
				throw new DAOException("Выберите папку для редактирование");
			}
			MainPageFoldersService service = (MainPageFoldersService)appContext.getContext().getBean("mainPageFoldersService");
			service.update(this.selected);
			GeneralUtil.addSuccessMessage("Сохранено успешно!");
			GeneralUtil.hideDialog("MpfUpdateDialog");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public MainPageFolders prepareCreate(){
		this.selected = new MainPageFolders();
		return this.selected;
	}

	private String breadcrumb = "Дит > Все меню";

	public String getBreadcrumb() {
		return breadcrumb;
	}

	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
	  this.appContext = appContext;
	}
}
