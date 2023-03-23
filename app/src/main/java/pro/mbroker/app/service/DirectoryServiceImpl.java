package pro.mbroker.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.mbroker.api.dto.request.DirectoryRequest;
import pro.mbroker.api.dto.response.DirectoryResponse;
import pro.mbroker.api.enums.*;
import pro.mbroker.app.exception.DirectoryExceptionHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DirectoryServiceImpl implements DirectoryService {

    @Override
    public DirectoryResponse getDirectory() {

        List<EnumItemDescription> employmentValues = getEnumItemDescription(EmploymentType.class);
        List<EnumItemDescription> realEstateValues = getEnumItemDescription(RealEstateType.class);
        List<EnumItemDescription> creditPurposeValues = getEnumItemDescription(CreditPurposeType.class);

        return DirectoryResponse.builder()
                .employmentTypes(getEnumDescription(employmentValues, EmploymentType.class))
                .realEstateTypes(getEnumDescription(realEstateValues, RealEstateType.class))
                .creditPurposeTypes(getEnumDescription(creditPurposeValues, CreditPurposeType.class))
                .build();
    }

    @Override
    public DirectoryResponse updateDirectory(DirectoryRequest request) {
        try {
            return DirectoryResponse.builder()
                    .employmentTypes(EnumDescription.builder()
                            .code(EmploymentType.class.getSimpleName())
                            .values(getEnumItemDescriptions(request.getEmploymentTypeCodes(), EmploymentType.class))
                            .build())
                    .realEstateTypes(EnumDescription.builder()
                            .code(RealEstateType.class.getSimpleName())
                            .values(getEnumItemDescriptions(request.getRealEstateTypeCodes(), RealEstateType.class))
                            .build())
                    .creditPurposeTypes(EnumDescription.builder()
                            .code(CreditPurposeType.class.getSimpleName())
                            .values(getEnumItemDescriptions(request.getCreditPurposeTypeCodes(), CreditPurposeType.class))
                            .build())
                    .build();
        } catch (IllegalArgumentException e) {
            String enumCode = e.getMessage().substring(e.getMessage().lastIndexOf(':') + 1).trim();
            throw new DirectoryExceptionHandler(e.getClass(), enumCode);
        }
    }

    private List<EnumItemDescription> getEnumItemDescriptions(List<String> codes, Class<? extends DirectoryEnumMarker> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(type -> codes.contains(type.getCode()))
                .map(type -> EnumItemDescription.builder()
                        .code(type.getCode())
                        .name(type.getName())
                        .description(type.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    private EnumDescription getEnumDescription(List<EnumItemDescription> enumItem, Class<? extends DirectoryEnumMarker> enumClass) {
        EnumDescription employmentTypes = EnumDescription.builder()
                .code(enumClass.getSimpleName())
                .values(enumItem)
                .build();
        return employmentTypes;
    }

    private List<EnumItemDescription> getEnumItemDescription(Class<? extends DirectoryEnumMarker> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(type -> EnumItemDescription.builder()
                        .code(type.name())
                        .name(type.getName())
                        .description(type.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
