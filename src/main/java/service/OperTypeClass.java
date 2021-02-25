package service;

public class OperTypeClass {

		public OperTypeClass(Long ot, String on) {
			this.operType = ot;
			this.operName = on;
		}
		
		Long operType;
		String operName;
		
		public Long getOperType() {
			return operType;
		}
		public void setOperType(Long operType) {
			this.operType = operType;
		}
		public String getOperName() {
			return operName;
		}
		public void setOperName(String operName) {
			this.operName = operName;
		}
	
}
