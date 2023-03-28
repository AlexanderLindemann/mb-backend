package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.DirectoryController;
import pro.mbroker.api.dto.response.EnumDescription;
import pro.mbroker.api.enums.RegionType;
import pro.mbroker.app.service.DirectoryService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectoryControllerImpl implements DirectoryController {
    private final DirectoryService directoryService;

    @Override
    public List<EnumDescription> getAllDirectory() {
        return directoryService.getAllDirectory();
    }

    @Override
    public List<EnumDescription> getAllRegion() {
        return directoryService.getAllRegion();
    }

    @Override
    public List<EnumDescription> getFilteredRegion(List<RegionType> include, List<RegionType> exclude) {
        return directoryService.getFilteredRegion(include, exclude);
    }

}
