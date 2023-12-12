package pro.mbroker.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.mbroker.api.enums.RealEstateType;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BankApplicationKey {
    private final UUID creditProgramId;
    private final RealEstateType realEstateType;

    public BankApplicationKey(UUID creditProgramId, RealEstateType realEstateType) {
        this.creditProgramId = creditProgramId;
        this.realEstateType = realEstateType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankApplicationKey that = (BankApplicationKey) o;
        return Objects.equals(creditProgramId, that.creditProgramId) &&
                realEstateType == that.realEstateType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(creditProgramId, realEstateType);
    }
}
