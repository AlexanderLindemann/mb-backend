package pro.mbroker.app;

import org.springframework.stereotype.Component;
import pro.mbroker.api.dto.MortgageCalculationDto;
import pro.mbroker.api.dto.request.BankApplicationRequest;
import pro.mbroker.api.dto.request.BankApplicationUpdateRequest;
import pro.mbroker.api.dto.request.BankContactRequest;
import pro.mbroker.api.dto.request.BankRequest;
import pro.mbroker.api.dto.request.BorrowerProfileRequest;
import pro.mbroker.api.dto.request.BorrowerRequest;
import pro.mbroker.api.dto.request.CalculatorRequest;
import pro.mbroker.api.dto.request.PartnerApplicationRequest;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TestData {
    public List<BankApplicationRequest> getBankApplication() {
        BankApplicationRequest bankApplicationRequest1 = new BankApplicationRequest()
                .setCreditProgramId(UUID.fromString("bfda8d66-d926-11ed-afa1-0242ac120002"))
                .setOverpayment(BigDecimal.valueOf(10000000))
                .setCreditTerm(20)
                .setDownPayment(BigDecimal.valueOf(1000000))
                .setMonthlyPayment(BigDecimal.valueOf(100000))
                .setRealEstatePrice(BigDecimal.valueOf(50000000));
        BankApplicationRequest bankApplicationRequest2 = new BankApplicationRequest()
                .setCreditProgramId(UUID.fromString("8222cb80-d928-11ed-afa1-0242ac120002"))
                .setOverpayment(BigDecimal.valueOf(10000000))
                .setCreditTerm(20)
                .setDownPayment(BigDecimal.valueOf(1000000))
                .setMonthlyPayment(BigDecimal.valueOf(100000))
                .setRealEstatePrice(BigDecimal.valueOf(50000000));
        return new ArrayList<>(List.of(bankApplicationRequest1, bankApplicationRequest2));
    }

    public List<BorrowerProfileRequest> getBorrowerProfileRequestList() {
        BorrowerProfileRequest borrowerProfileRequest1 = new BorrowerProfileRequest()
                .setEmail("test@test.com")
                .setFirstName("TestFirstName")
                .setLastName("TestLastName");
        BorrowerProfileRequest borrowerProfileRequest2 = new BorrowerProfileRequest()
                .setEmail("test2@test.com")
                .setFirstName("TestFirstName2")
                .setLastName("TestLastName2");
        BorrowerProfileRequest borrowerProfileRequest3 = new BorrowerProfileRequest()
                .setEmail("test3@test.com")
                .setFirstName("TestFirstName3")
                .setLastName("TestLastName3");
        return new ArrayList<>(List.of(borrowerProfileRequest1, borrowerProfileRequest2, borrowerProfileRequest3));
    }

    public MortgageCalculationDto getMortgageCalculation() {
        return new MortgageCalculationDto()
                .setCreditTerm(20)
                .setDownPayment(BigDecimal.valueOf(1000000))
                .setRealEstatePrice(BigDecimal.valueOf(50000000))
                .setIsMaternalCapital(true);
    }

    public BankApplicationUpdateRequest getBankApplicationUpdateRequest() {
        return new BankApplicationUpdateRequest()
                .setCreditProgramId(UUID.fromString("8222cb80-d928-11ed-afa1-0242ac120002"))
                .setOverpayment(BigDecimal.valueOf(10000000))
                .setMonthlyPayment(BigDecimal.valueOf(50000))
                .setCreditTerm(240)
                .setRealEstatePrice(BigDecimal.valueOf(50000000))
                .setDownPayment(BigDecimal.valueOf(1000000))
                .setMainBorrowerId(UUID.fromString("1348b508-f476-11ed-a05b-0242ac120003"));
    }

    public PartnerApplicationRequest getPartnerApplicationRequest() {
        return new PartnerApplicationRequest()
                .setBankApplications(getBankApplication())
                .setMainBorrower(getBorrowerProfileRequestList().get(0))
                .setCreditPurposeType(CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION)
                .setRealEstateType(RealEstateType.APARTMENT)
                .setRealEstateId(UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"))
                .setMortgageCalculation(getMortgageCalculation());
    }

    public BankApplicationRequest getBankApplicationRequest() {
        return new BankApplicationRequest()
                .setId(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"))
                .setOverpayment(BigDecimal.valueOf(7000000))
                .setDownPayment(BigDecimal.valueOf(1000000))
                .setMonthlyPayment(BigDecimal.valueOf(100000))
                .setCreditTerm(30)
                .setRealEstatePrice(BigDecimal.valueOf(1000000000));
    }

    public BankRequest getBankRequest() {
        return new BankRequest()
                .setName("Test Bank")
                .setBankContacts(getBankContacts());
    }

    private List<BankContactRequest> getBankContacts() {
        new BankContactRequest().setEmail("test email 1")
                .setFullName("test full name 1");
        new BankContactRequest().setEmail("test email 2")
                .setFullName("test full name 2");
        return List.of(new BankContactRequest().setEmail("test email 1")
                .setFullName("test full name 1"), new BankContactRequest().setEmail("test email 2")
                .setFullName("test full name 2"));
    }

    public BorrowerRequest getBorrowerRequest() {
        return new BorrowerRequest()
                .setMainBorrower(getBorrowerProfileRequestList().get(0))
                .setCoBorrower(List.of(getBorrowerProfileRequestList().get(1), getBorrowerProfileRequestList().get(2)));

    }

    public CalculatorRequest getCalculatorRequest() {
        return new CalculatorRequest()
                .setRealEstateId(UUID.fromString("2b8850b2-d930-11ed-afa1-0242ac120002"))
                .setCreditTerm(5)
                .setCreditPurposeType(CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION)
                .setDownPayment(BigDecimal.valueOf(10000000))
                .setRealEstatePrice(BigDecimal.valueOf(20000000))
                .setRealEstateType(RealEstateType.APARTMENT)
                .setIsMaternalCapital(true);
    }
}
