package pro.mbroker.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.mbroker.api.dto.BorrowerApplicationDto;
import pro.mbroker.app.entity.BorrowerApplication;

import java.util.List;

@Mapper
public interface BorrowerApplicationMapper {

    @Mapping(source = "borrowerApplication.creditProgram.bank.name", target = "bankName")
    @Mapping(source = "borrowerApplication.creditProgram.programName", target = "creditProgramName")
    @Mapping(source = "borrowerApplication.creditProgram.baseRate", target = "interestRate")
    BorrowerApplicationDto toDto(BorrowerApplication borrowerApplication);

    List<BorrowerApplicationDto> toDtoList(List<BorrowerApplication> borrowerApplications);

}