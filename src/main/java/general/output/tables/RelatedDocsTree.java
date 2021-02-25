package general.output.tables;

import general.Validation;

public class RelatedDocsTree {

	private Long id;
	private String context;
	private Long contextId;
	private boolean isActive;
	private String documentName;

	public Long getContextId() {
		return contextId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
		// System.err.println("CONTEXT_ID =>" + contextId);
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentName() {
		if (!Validation.isEmptyString(documentName)) {
			return documentName;
		}
		
		if (context.equals("request")) {
			return "Заявка";
		} else if (context.equals("request_out")) {
			return "Внешняя заявка";
		} else if (context.equals("bkpf")) {
			return "Счет фактура";
		} else if (context.equals("order")) {
			return "Заказ";
		} else if (context.equals("invoice")) {
			return "Накладная";
		}

		return null;
	}
}
