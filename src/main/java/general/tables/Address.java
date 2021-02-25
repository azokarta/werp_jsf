package general.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "address")
@javax.persistence.SequenceGenerator(name="seq_address_id",sequenceName="seq_address_id", allocationSize = 1)
public class Address implements Cloneable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5875814437414972808L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_address_id")
    private Long addr_id;
	
	/*
	 *  ADDR_TYPES:
	 *  1 - Home 
	 *  2 - Work 
	 *  3 - Registered
	 *  4 - Additional
	 */
	@Column(name = "addr_type")
    private int addrType;
	
	public static final int TYPE_HOME = 1;
	public static final int TYPE_WORK = 2;
	public static final int TYPE_REGISTERED = 3;
	public static final int TYPE_ADDITIONAL = 4;
	
	@Column(name = "address")
    private String address;
	
	@Column(name = "countryid")
    private Long countryId;
	
	@Column(name = "stateid")
    private Long stateId;
	
	@Column(name = "cityid")
    private Long cityId;
	
	@Column(name = "regid")
    private Long regId;
	
	@Column(name = "street")
    private String street;
	
	@Column(name = "ap_number")
    private String ap_number;
	
	@Column(name = "ap_drob")
    private String ap_drob;
	
	@Column(name = "flat_number")
    private Long flat_number;
	
	@Column(name = "tel_dom")
    private String telDom;
	
	@Column(name = "tel_mob1")
    private String telMob1;
	
	@Column(name = "tel_mob2")
    private String telMob2;
	
	@Column(name = "customer_id")
    private Long customerId;

	@Column(name = "avenue")
    private String avenue;
	
	@Column(name = "village")
    private String village;
	
	@Column(name = "microdistrict")
    private String microdistrict;
	
	@Column(name = "postal_code")
    private String postalCode;
	
	public String getAp_number() {
		return ap_number;
	}

	public void setAp_number(String ap_number) {
		this.ap_number = ap_number;
	}

	public String getAp_drob() {
		return ap_drob;
	}

	public void setAp_drob(String ap_drob) {
		this.ap_drob = ap_drob;
	}
	
	public String getAvenue() {
		return avenue;
	}

	public void setAvenue(String avenue) {
		this.avenue = avenue;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public String getMicrodistrict() {
		return microdistrict;
	}

	public void setMicrodistrict(String microdistrict) {
		this.microdistrict = microdistrict;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Long getAddr_id() {
		return addr_id;
	}

	public void setAddr_id(Long addr_id) {
		this.addr_id = addr_id;
	}

	public int getAddrType() {
		return addrType;
	}

	public void setAddrType(int addrType) {
		this.addrType = addrType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public Long getRegId() {
		return regId;
	}

	public void setRegId(Long regId) {
		this.regId = regId;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Long getFlat_number() {
		return flat_number;
	}

	public void setFlat_number(Long flat_number) {
		this.flat_number = flat_number;
	}

	public String getTelDom() {
		return telDom;
	}

	public void setTelDom(String telDom) {
		this.telDom = telDom;
	}

	public String getTelMob1() {
		return telMob1;
	}

	public void setTelMob1(String telMob1) {
		this.telMob1 = telMob1;
	}

	public String getTelMob2() {
		return telMob2;
	}

	public void setTelMob2(String telMob2) {
		this.telMob2 = telMob2;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	
	@Override
    public Address clone() throws CloneNotSupportedException {
        return (Address) super.clone();
    }
	
}
