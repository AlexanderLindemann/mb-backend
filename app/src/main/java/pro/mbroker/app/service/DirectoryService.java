package pro.mbroker.app.service;

import pro.mbroker.api.dto.request.DirectoryRequest;
import pro.mbroker.api.dto.response.DirectoryResponse;

public interface DirectoryService {
    DirectoryResponse getDirectory();

    DirectoryResponse updateDirectory(DirectoryRequest request);
}
