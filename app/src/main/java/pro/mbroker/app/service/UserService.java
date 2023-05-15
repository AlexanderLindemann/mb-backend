package pro.mbroker.app.service;

import pro.mbroker.api.dto.response.UserResponse;

public interface UserService {

    /**
     * Метод возвращающий информацию о пользователе из token'a
     * @param token jwt token, содержащий данные о пользователе
     * @return Данные о пользователе
     */
    UserResponse getUserInformation(String token);

}
