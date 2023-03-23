package pro.mbroker.app.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.mbroker.api.controller.DirectoryController;
import pro.mbroker.api.dto.request.DirectoryRequest;
import pro.mbroker.api.dto.response.DirectoryResponse;
import pro.mbroker.app.service.DirectoryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectoryControllerImpl implements DirectoryController {
    private final DirectoryService directoryService;

    @Override
    public DirectoryResponse getDirectory() {
        return directoryService.getDirectory();
    }

    @Override
    public DirectoryResponse updateDirectory(DirectoryRequest directoryRequest) {
        return directoryService.updateDirectory(directoryRequest);
    }

}
