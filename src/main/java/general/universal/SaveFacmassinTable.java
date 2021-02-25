package general.universal;

import general.tables.Bkpf;
import general.tables.Bseg;

import java.util.ArrayList;
import java.util.List;

public class SaveFacmassinTable {
	private Bkpf p_bkpf = new Bkpf();
	private List<Bseg> l_bseg = new ArrayList<Bseg>();
	private Long payment_schedule_id;
	public Bkpf getP_bkpf() {
		return p_bkpf;
	}
	public void setP_bkpf(Bkpf p_bkpf) {
		this.p_bkpf = p_bkpf;
	}
	public List<Bseg> getL_bseg() {
		return l_bseg;
	}
	public void setL_bseg(List<Bseg> l_bseg) {
		this.l_bseg = l_bseg;
	}
	public Long getPayment_schedule_id() {
		return payment_schedule_id;
	}
	public void setPayment_schedule_id(Long payment_schedule_id) {
		this.payment_schedule_id = payment_schedule_id;
	}
}
