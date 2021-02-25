package f4;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "updateF4Bean")
@ApplicationScoped
public class UpdateF4 {
	
	@ManagedProperty(value="#{actiontypeF4Bean}")
	private ActionTypeF4 actiontypeF4Bean;
	
	@ManagedProperty(value="#{addrTypeF4Bean}")
	private AddrTypeF4 addrTypeF4Bean;
	
	@ManagedProperty(value="#{bankPartnerF4Bean}")
	private BankPartnerF4 bankPartnerF4Bean;
	
	@ManagedProperty(value="#{blartF4Bean}")
	private BlartF4 blartF4Bean;
	
	@ManagedProperty(value="#{bonusTypeF4Bean}")
	private BonusTypeF4 bonusTypeF4Bean;
	
	@ManagedProperty(value="#{branchF4Bean}")
	private BranchF4 branchF4Bean;
	
	@ManagedProperty(value="#{bschlF4Bean}")
	private BschlF4 bschlF4Bean;	
	
	@ManagedProperty(value="#{bukrsF4Bean}")
	private BukrsF4 bukrsF4Bean;	
	
	@ManagedProperty(value="#{businessAreaF4Bean}")
	private BusinessAreaF4 businessAreaF4Bean;	
	
	@ManagedProperty(value="#{cityF4Bean}")
	private CityF4 cityF4Bean;
	
	@ManagedProperty(value="#{cityregF4Bean}")
	private CityregF4 cityregF4Bean;
	
	@ManagedProperty(value="#{contractOperF4Bean}")
	private ContractOperF4 contractOperF4Bean;
	
	@ManagedProperty(value="#{contractStatusF4Bean}")
	private ContractStatusF4 contractStatusF4Bean;
	
	@ManagedProperty(value="#{contractTypeF4Bean}")
	private ContractTypeF4 contractTypeF4Bean;
	
	@ManagedProperty(value="#{countryF4Bean}")
	private CountryF4 countryF4Bean;
	
	@ManagedProperty(value="#{currencyF4Bean}")
	private CurrencyF4 currencyF4Bean;
	
	@ManagedProperty(value="#{departmentF4Bean}")
	private DepartmentF4 departmentF4Bean;
	
	@ManagedProperty(value="#{exchangeRateF4Bean}")
	private ExchangeRateF4 exchangeRateF4Bean;
	
	@ManagedProperty(value="#{hkontF4Bean}")
	private HkontF4 hkontF4Bean;
	
	@ManagedProperty(value="#{matnrF4Bean}")
	private MatnrF4 matnrF4Bean;
	
	@ManagedProperty(value="#{meinsF4Bean}")
	private MeinsF4 meinsF4Bean;
	
	@ManagedProperty(value="#{orderStatusF4Bean}")
	private OrderStatusF4 orderStatusF4Bean;
	
	@ManagedProperty(value="#{paymentTemplateF4Bean}")
	private PaymentTemplateF4 paymentTemplateF4Bean;
	
	@ManagedProperty(value="#{positionF4Bean}")
	private PositionF4 positionF4Bean;
	
	@ManagedProperty(value="#{priceListF4Bean}")
	private PriceListF4 priceListF4Bean;
	
	@ManagedProperty(value="#{roleF4Bean}")
	private RoleF4 roleF4Bean;
		
	@ManagedProperty(value="#{stateF4Bean}")
	private StateF4 stateF4Bean;
	
	@ManagedProperty(value="#{werksF4Bean}")
	private WerksF4 werksF4Bean;
	
	
	
	public void setActiontypeF4Bean(ActionTypeF4 actiontypeF4Bean) {
		this.actiontypeF4Bean = actiontypeF4Bean;
	}



	public void setAddrTypeF4Bean(AddrTypeF4 addrTypeF4Bean) {
		this.addrTypeF4Bean = addrTypeF4Bean;
	}



	public void setBankPartnerF4Bean(BankPartnerF4 bankPartnerF4Bean) {
		this.bankPartnerF4Bean = bankPartnerF4Bean;
	}



	public void setBlartF4Bean(BlartF4 blartF4Bean) {
		this.blartF4Bean = blartF4Bean;
	}



	public void setBonusTypeF4Bean(BonusTypeF4 bonusTypeF4Bean) {
		this.bonusTypeF4Bean = bonusTypeF4Bean;
	}



	public void setBranchF4Bean(BranchF4 branchF4Bean) {
		this.branchF4Bean = branchF4Bean;
	}



	public void setBschlF4Bean(BschlF4 bschlF4Bean) {
		this.bschlF4Bean = bschlF4Bean;
	}



	public void setBukrsF4Bean(BukrsF4 bukrsF4Bean) {
		this.bukrsF4Bean = bukrsF4Bean;
	}



	public void setBusinessAreaF4Bean(BusinessAreaF4 businessAreaF4Bean) {
		this.businessAreaF4Bean = businessAreaF4Bean;
	}



	public void setCityF4Bean(CityF4 cityF4Bean) {
		this.cityF4Bean = cityF4Bean;
	}



	public void setCityregF4Bean(CityregF4 cityregF4Bean) {
		this.cityregF4Bean = cityregF4Bean;
	}



	public void setContractOperF4Bean(ContractOperF4 contractOperF4Bean) {
		this.contractOperF4Bean = contractOperF4Bean;
	}



	public void setContractStatusF4Bean(ContractStatusF4 contractStatusF4Bean) {
		this.contractStatusF4Bean = contractStatusF4Bean;
	}



	public void setContractTypeF4Bean(ContractTypeF4 contractTypeF4Bean) {
		this.contractTypeF4Bean = contractTypeF4Bean;
	}



	public void setCountryF4Bean(CountryF4 countryF4Bean) {
		this.countryF4Bean = countryF4Bean;
	}



	public void setCurrencyF4Bean(CurrencyF4 currencyF4Bean) {
		this.currencyF4Bean = currencyF4Bean;
	}



	public void setDepartmentF4Bean(DepartmentF4 departmentF4Bean) {
		this.departmentF4Bean = departmentF4Bean;
	}



	public void setExchangeRateF4Bean(ExchangeRateF4 exchangeRateF4Bean) {
		this.exchangeRateF4Bean = exchangeRateF4Bean;
	}



	public void setHkontF4Bean(HkontF4 hkontF4Bean) {
		this.hkontF4Bean = hkontF4Bean;
	}



	public void setMatnrF4Bean(MatnrF4 matnrF4Bean) {
		this.matnrF4Bean = matnrF4Bean;
	}



	public void setMeinsF4Bean(MeinsF4 meinsF4Bean) {
		this.meinsF4Bean = meinsF4Bean;
	}



	public void setOrderStatusF4Bean(OrderStatusF4 orderStatusF4Bean) {
		this.orderStatusF4Bean = orderStatusF4Bean;
	}



	public void setPaymentTemplateF4Bean(PaymentTemplateF4 paymentTemplateF4Bean) {
		this.paymentTemplateF4Bean = paymentTemplateF4Bean;
	}



	public void setPositionF4Bean(PositionF4 positionF4Bean) {
		this.positionF4Bean = positionF4Bean;
	}



	public void setPriceListF4Bean(PriceListF4 priceListF4Bean) {
		this.priceListF4Bean = priceListF4Bean;
	}



	public void setRoleF4Bean(RoleF4 roleF4Bean) {
		this.roleF4Bean = roleF4Bean;
	}



	public void setStateF4Bean(StateF4 stateF4Bean) {
		this.stateF4Bean = stateF4Bean;
	}



	public void setWerksF4Bean(WerksF4 werksF4Bean) {
		this.werksF4Bean = werksF4Bean;
	}



	public void updateAllF4()
	{
		actiontypeF4Bean.updateF4();
		addrTypeF4Bean.updateF4();
		bankPartnerF4Bean.updateF4();
		blartF4Bean.updateF4();
		bonusTypeF4Bean.updateF4();
		branchF4Bean.updateF4();
		bschlF4Bean.updateF4();
		bukrsF4Bean.updateF4();
		businessAreaF4Bean.updateF4();
		cityF4Bean.updateF4();
		cityregF4Bean.updateF4();
		contractOperF4Bean.updateF4();
		contractStatusF4Bean.init();
		contractTypeF4Bean.updateF4();
		countryF4Bean.updateF4();
		currencyF4Bean.updateF4();
		departmentF4Bean.updateF4();
		exchangeRateF4Bean.updateF4();
		hkontF4Bean.updateF4();
		matnrF4Bean.updateF4();
		meinsF4Bean.updateF4();
		orderStatusF4Bean.updateF4();
		paymentTemplateF4Bean.updateF4();
		positionF4Bean.updateF4();
		priceListF4Bean.updateF4();
		roleF4Bean.updateF4();
		stateF4Bean.updateF4();
		werksF4Bean.updateF4();
	}
	
}	