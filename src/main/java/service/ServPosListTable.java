package service;

import general.tables.Matnr;
import general.tables.ServicePos;

public class ServPosListTable {

	public ServPosListTable() {
		this.group = 1;
		this.warranty = false;
		this.matnr = new Matnr();
		this.servPos = new ServicePos();
		this.is_premi = false;
	}

	public ServPosListTable(int a_index, String a_bukrs, String cur) {
		// TODO Auto-generated constructor stub
		this.setIndex(a_index);
		this.matnr = new Matnr();
		this.servPos = new ServicePos();
		this.servPos.setQuantity(1L);
		this.servPos.setOperation(1L);
		this.servPos.setMatnr_price(0L);
		this.servPos.setCurrency(cur);
		this.servPos.setInfo1("");
		this.servPos.setInfo2("");
		this.group = 1;
		this.warranty = false;
		this.is_premi = false;
	}

	private int index;
	private Matnr matnr;
	private double sum;
	private double sum_sc;
	private int group;
	public ServicePos servPos;
	private boolean warranty;
	public boolean dis_mat = false;
	public boolean dis_pr = false;
	public boolean dis_qq = false;
	private boolean is_premi;

	public boolean isDis_pr() {
		return dis_pr;
	}

	public void setDis_pr(boolean dis_pr) {
		this.dis_pr = dis_pr;
	}

	public boolean isDis_qq() {
		return dis_qq;
	}
	public void setDis_qq(boolean dis_qq) {
		this.dis_qq = dis_qq;
	}

	public boolean isDis_mat() {
		return dis_mat;
	}

	public void setDis_mat(boolean dis_mat) {
		this.dis_mat = dis_mat;
	}

	public boolean isIs_premi() {
		return is_premi;
	}

	public void setIs_premi(boolean is_premi) {
		this.is_premi = is_premi;
	}

	public boolean isWarranty() {
		return warranty;
	}

	public void setWarranty(boolean warranty) {
		this.warranty = warranty;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Matnr getMatnr() {
		return matnr;
	}

	public void setMatnr(Matnr matnr) {
		this.matnr = matnr;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getSum_sc() {
		return sum_sc;
	}

	public void setSum_sc(double sum_sc) {
		this.sum_sc = sum_sc;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public ServicePos getServPos() {
		return servPos;
	}

	public void setServPos(ServicePos servPos) {
		this.servPos = servPos;
	}

}
