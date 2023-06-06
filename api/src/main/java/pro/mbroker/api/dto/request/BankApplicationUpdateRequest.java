package pro.mbroker.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankApplicationUpdateRequest {

    private UUID creditProgramId;

    private UUID mainBorrowerId;

    private BigDecimal monthlyPayment;

    private BigDecimal realEstatePrice;

    private BigDecimal downPayment;

    private Integer creditTerm;

    private BigDecimal overpayment;

}
