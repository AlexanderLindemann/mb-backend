package pro.mbroker.app;

import org.springframework.mock.web.MockMultipartFile;
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
import pro.mbroker.api.dto.response.PublicKeyResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.Insurance;
import pro.mbroker.api.enums.PaymentSource;
import pro.mbroker.api.enums.RealEstateType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
                .setRealEstateType(RealEstateType.APARTMENT)
                .setRealEstatePrice(BigDecimal.valueOf(50000000));
        BankApplicationRequest bankApplicationRequest2 = new BankApplicationRequest()
                .setCreditProgramId(UUID.fromString("8222cb80-d928-11ed-afa1-0242ac120002"))
                .setOverpayment(BigDecimal.valueOf(10000000))
                .setCreditTerm(20)
                .setDownPayment(BigDecimal.valueOf(1000000))
                .setMonthlyPayment(BigDecimal.valueOf(100000))
                .setRealEstateType(RealEstateType.APARTMENT)
                .setRealEstatePrice(BigDecimal.valueOf(50000000));
        return new ArrayList<>(List.of(bankApplicationRequest1, bankApplicationRequest2));
    }

    public List<BorrowerProfileRequest> getBorrowerProfileRequestList() {
        BorrowerProfileRequest borrowerProfileRequest1 = new BorrowerProfileRequest()
                .setEmail("test@test.com")
                .setFirstName("TestFirstName")
                .setLastName("TestLastName")
                .setPhoneNumber("12345567");
        BorrowerProfileRequest borrowerProfileRequest2 = new BorrowerProfileRequest()
                .setEmail("test2@test.com")
                .setFirstName("TestFirstName2")
                .setPhoneNumber("12345567")
                .setLastName("TestLastName2");
        BorrowerProfileRequest borrowerProfileRequest3 = new BorrowerProfileRequest()
                .setEmail("test3@test.com")
                .setFirstName("TestFirstName3")
                .setPhoneNumber("12345567")
                .setLastName("TestLastName3");
        return new ArrayList<>(List.of(borrowerProfileRequest1, borrowerProfileRequest2, borrowerProfileRequest3));
    }

    public MockMultipartFile getMockMultipartFile() {
        return new MockMultipartFile(
                "file",
                "filename.txt",
                "text/plain",
                "Тестовое содержимое файла".getBytes()
        );
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
                .setRealEstateTypes(List.of(RealEstateType.APARTMENT, RealEstateType.ROOM))
                .setRealEstateId("2b8850b2-d930-11ed-afa1-0242ac120002")
                .setMortgageCalculation(getMortgageCalculation());
    }

    public PartnerApplicationRequest getShortPartnerApplicationRequest() {
        return new PartnerApplicationRequest()
                .setInsurances(List.of(Insurance.LIFE_INSURANCE, Insurance.TITLE_INSURANCE))
                .setPaymentSource(List.of(PaymentSource.APARTMENT_SALE, PaymentSource.MILITARY_MORTGAGE))
                .setMaternalCapitalAmount(BigDecimal.valueOf(120000))
                .setSubsidyAmount(BigDecimal.valueOf(30000));
    }

    public PartnerApplicationRequest getShortPartnerApplicationRequestWithNullValues() {
        return new PartnerApplicationRequest()
                .setInsurances(null)
                .setPaymentSource(null)
                .setMaternalCapitalAmount(BigDecimal.valueOf(0))
                .setSubsidyAmount(BigDecimal.valueOf(0));
    }

    public BankApplicationRequest getBankApplicationRequest() {
        return new BankApplicationRequest()
                .setId(UUID.fromString("3b339aa4-5462-485a-9118-5922cd948566"))
                .setCreditProgramId(UUID.fromString("8222cb80-d928-11ed-afa1-0242ac120002"))
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
                .setRealEstateId("2b8850b2-d930-11ed-afa1-0242ac120002")
                .setCreditTerm(5)
                .setCreditPurposeType(CreditPurposeType.PURCHASE_UNDER_CONSTRUCTION)
                .setDownPayment(BigDecimal.valueOf(10000000))
                .setRealEstatePrice(BigDecimal.valueOf(20000000))
                .setRealEstateTypes(List.of(RealEstateType.APARTMENT))
                .setIsMaternalCapital(true);
    }

    public PublicKeyResponse createMockPublicKeyResponse() {
        PublicKeyResponse response = new PublicKeyResponse();
        PublicKeyResponse.Key key = new PublicKeyResponse.Key();
        key.setKty("RSA");
        key.setN("AP2_KR6X--PKANTPEvDWUTC_QzmWYukfSW9Wmd8TBMzTpBqu5nqpFFk81j7jhglLNWngRgtFxsaNAlaouPsCS4B_eIatURSH8KUJ3d4YRMTq3OKy_Lxe7fL5SY3M9yZSoaGif0LG3zXaQPD4FhSSJAKprB9DgtG2nIO1TfrV7KLjQm72oWMNUNVMPobUbEhZu8TFf_FNCUHgwj9EjoJZeYF48p1vB5t_tJrtOP4ySUhG1fvqZCTZGBUvlgkuqW2dF0t1wIquWEY_-ROFEsce0JuQomua2ZLXvwdAv3tYnIWZywBXtCpTkQEcGvPcVxCWtYSPmP-mUX2p-YJqjELa44M=");
        key.setE("AQAB");
        response.setKeys(Collections.singletonList(key));
        return response;
    }
}
