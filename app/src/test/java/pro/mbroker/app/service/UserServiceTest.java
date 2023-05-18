package pro.mbroker.app.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.mbroker.api.dto.response.UserResponse;
import pro.mbroker.app.service.impl.UserServiceImpl;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest extends AbstractServiceTest{

    @Autowired
    private UserServiceImpl userServiceImpl;

    private static final List<String> PERMISSION_SCOPES = List.of("UPDATE_STATEMENT_ANY",
            "REAL_ESTATE_AND_CADASTRAL_NUMBER_SEARCH",
            "MONITORING",
            "ADMINISTRATION_ORGANIZATIONS_READ",
            "TRADE_DELETE_OWN",
            "CREATE_EXTRACTION_DDU",
            "SYSTEM_ADMINISTRATOR",
            "DOCUMENT_REQUESTS_ORGANIZATION",
            "ESIGN_DELIVERY_BY_CURRIER",
            "TRADE_CREATE",
            "INVITE_USER_MANAGER",
            "DOCUMENT_REQUESTS",
            "GET_STATEMENT",
            "ADMINISTRATION_APP_CONFIGURATION",
            "SIGNATURE_IDENTIFICATION_TYPE_SELECT",
            "TRADE_ACCESS",
            "SUBSCRIBE_REQUEST",
            "GET_MORTGAGE_LIST",
            "MANAGE_ORGANIZATION_LIST",
            "GET_STATEMENT_RESULT",
            "SIGN_GOS_KEY",
            "CREATE_EXTRACTION_RIGHT_MOVEMENT",
            "EDOC_DKP_EDIT_ALL_LK",
            "MAKE_STATEMENT_ON_AGREEMENT",
            "DEVELOPMENT",
            "EDOC_DKP_EDIT_OWN",
            "EDOC_DKP_READ",
            "MANAGE_USER_LIST",
            "DICTIONARY_PERSON_ADMINISTRATION",
            "CREATE_EXTRACTION_RIGHTS",
            "MAKE_STATEMENT_AGREED",
            "MANAGE_USER_DOCUMENTS",
            "SEND_STATEMENT",
            "TRADE_ACCEPT",
            "TRADE_INVITE_ASSIGN",
            "GET_STATEMENT_LIST",
            "SYSTEM_SUPPORT_LOGIN_AS",
            "PAY",
            "SETTING_ALLOWED_IP",
            "CHECK_SIGNATURE",
            "GET_USER_LIST",
            "GET_STATISTICS",
            "CREATE_SIGNATURE_NEW",
            "ADMINISTRATION_NOTIFICATION",
            "USE_SIGNATURE",
            "DELETE_STATEMENT_FOREIGN",
            "MANAGE_GROUP_PERMISSION_LIST",
            "GET_GROUP_PERMISSION_LIST",
            "UPDATE_STATEMENT_OWN",
            "EDIT_OWN_PROFILE",
            "ADMINISTRATION_FILES",
            "CREATE_SIGNATURE_DRAFT_OR_EXISTENT",
            "GET_ORGANIZATION_LIST",
            "ELECTRONIC_DOCUMENT_ALLOWED",
            "GET_SIGNATURE_LIST",
            "DELETE_STATEMENT_OWN",
            "GET_EXTRACTION_LIST",
            "VISUALIZE_SIGNATURE",
            "UPDATE_STATEMENT_FOREIGN",
            "DOCUMENT_SEND_INTERNAL");

    @Test
    public void testGetUserInformation() {
        UserResponse userInformation = userServiceImpl.getUserInformation(apiToken);
        Assert.assertEquals(PERMISSION_SCOPES, userInformation.getPermissions());
    }

}
