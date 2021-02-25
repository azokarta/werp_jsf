package logistics.revision;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.services.RevisionService;
import general.tables.Revision;
import user.User;

@ManagedBean(name = "logRevActionBean")
@ViewScoped
public class ActionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -235163488825948092L;

	Revision revision;

	public void initBean(Revision revision) {
		this.revision = revision;
		prepareButtons();
	}

	private List<Button> buttonList;

	public List<Button> getButtonList() {
		return buttonList;
	}

	public void setButtonList(List<Button> buttonList) {
		this.buttonList = buttonList;
	}

	private void prepareButtons() {
		buttonList = new ArrayList<>();
		if (revision != null) {
			if (revision.getStatus().equals(Revision.STATUS_NEW)) {
				Button b1 = new Button("update", "Редактировать", "post");
				Button b2;
				// List<RevResponsible> revResList =
				// revision.getRevResponsibles();
				// if(revResList == null || revResList.size() == 0){
				// b2 = new Button("appoint_members", "Назначить членов
				// комиссии", "post");
				// }else{
				// //b2 = new Button("start", "Начать", "post");
				// }
				b2 = new Button("generate", "Сформировать акт", "post");
				buttonList.add(b1);
				buttonList.add(b2);
			} else if (revision.getStatus().equals(Revision.STATUS_IN_ACTION)) {
				Button b = new Button("generate", "Сформировать акт", "post");
				buttonList.add(b);

				Button b1 = new Button("finish", "Завершить", "post");
				buttonList.add(b1);
			} else if (Revision.STATUS_FINISHED.equals(revision.getStatus())) {
				Button b = new Button("return_to_action", "Вернуть в действию", "post");
				buttonList.add(b);
				
				Button b1 = new Button("to_close", "Распределить материалы и закрыть", "post");
				buttonList.add(b1);
			}
		}
	}

	public class Button {
		private String name;
		private String label;
		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Button(String name, String label, String type) {
			super();
			this.name = name;
			this.label = label;
			this.type = type;
		}

		public void doAction() {
			if (getName().equals("update")) {
				GeneralUtil.doRedirect("/logistics/revision/Update.xhtml?id=" + revision.getId());
			} else if (getName().equals("generate")) {
				GeneralUtil.doRedirect("/logistics/revision/title/Create.xhtml?revId=" + revision.getId());
			} else if ("finish".equals(getName())) {
				finishRevision();
			} else if ("return_to_action".equals(getName())) {
				returnToAction();
			}else if("to_close".equals(getName())){
				GeneralUtil.showDialog("matnrPostingWriteoffDialog");
			}
		}
	}

	/***************** ACTIONS *******************/

	private void finishRevision() {
		RevisionService revService = (RevisionService) appContext.getContext().getBean("revisionService");
		try {
			revService.finish(revision, userData);
			GeneralUtil.doRedirect("/logistics/revision/View.xhtml?id=" + revision.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void returnToAction() {
		RevisionService revService = (RevisionService) appContext.getContext().getBean("revisionService");
		try {
			revService.returnToAction(revision, userData);
			GeneralUtil.doRedirect("/logistics/revision/View.xhtml?id=" + revision.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty("#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
