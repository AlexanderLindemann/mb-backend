package pro.mbroker.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.request.DirectoryRequest;
import pro.mbroker.api.dto.response.DirectoryResponse;
import pro.mbroker.api.enums.CreditPurposeType;
import pro.mbroker.api.enums.EmploymentType;
import pro.mbroker.api.enums.RealEstateType;
import pro.mbroker.app.exception.DirectoryExceptionHandler;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DirectoryServiceImpl implements DirectoryService {

    @Override
    public DirectoryResponse getDirectory() {
        return DirectoryResponse.builder()
                .employmentTypes(Arrays.asList(EmploymentType.values()))
                .realEstateTypes(Arrays.asList(RealEstateType.values()))
                .creditPurposeTypes(Arrays.asList(CreditPurposeType.values()))
                .build();
    }

    @Override
    public DirectoryResponse updateDirectory(DirectoryRequest request) {
        try {
            return DirectoryResponse.builder()
                    .employmentTypes(request.getEmploymentTypeCodes().stream()
                            .map(EmploymentType::valueOf)
                            .collect(Collectors.toList()))
                    .realEstateTypes(request.getRealEstateTypeCodes().stream()
                            .map(RealEstateType::valueOf)
                            .collect(Collectors.toList()))
                    .creditPurposeTypes(request.getCreditPurposeTypeCodes().stream()
                            .map(CreditPurposeType::valueOf)
                            .collect(Collectors.toList()))
                    .build();
        } catch (IllegalArgumentException e) {
            String enumCode = e.getMessage().substring(e.getMessage().lastIndexOf(':') + 1).trim();
            throw new DirectoryExceptionHandler(e.getClass(), enumCode);
        }
    }
}
