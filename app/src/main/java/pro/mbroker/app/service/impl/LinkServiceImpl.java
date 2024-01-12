package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.UrlMappings;
import pro.mbroker.app.repository.BorrowerProfileRepository;
import pro.mbroker.app.service.LinkService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final BorrowerProfileRepository borrowerProfileRepository;

    @Override
    @Transactional
    public void addLinksByProfiles(List<BorrowerProfile> borrowerProfiles, HttpServletRequest httpRequest) {
        for (BorrowerProfile borrowerProfile : borrowerProfiles) {
            if (borrowerProfile.getLink() == null) {
                UrlMappings linkTable = createLinkTableForBorrowerProfile(borrowerProfile, httpRequest);
                borrowerProfile.setLink(linkTable);
            }
        }
        borrowerProfileRepository.saveAll(borrowerProfiles);
    }

    private UrlMappings createLinkTableForBorrowerProfile(BorrowerProfile borrowerProfile, HttpServletRequest httpRequest) {
        String prefix = determinePrefix(httpRequest.getRequestURL().toString());
        UrlMappings urlMappings = new UrlMappings();
        urlMappings.setShortLink(generateRandomString(prefix));
        urlMappings.setFullLink(buildFullLink(borrowerProfile, prefix));
        return urlMappings;
    }

    private String generateRandomString(String prefix) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(prefix);
        String alphaNumeric = "abcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < 8; i++) {
            char randomChar = alphaNumeric.charAt(random.nextInt(alphaNumeric.length()));
            sb.append(randomChar);
        }
        return sb.toString();
    }

    private String determinePrefix(String baseUrl) {
        if (baseUrl.contains("mb-dev-dc1")) {
            return "https://stage-cabinet.smartdeal.pro/";
        } else if (baseUrl.contains("trial")) {
            return "https://trial-cabinet.smartdeal.pro/";
        } else if (baseUrl.contains("localhost:8080")) {
            return "https://localhost:3000/";
        } else {
            return "https://my.smartdeal.pro/";
        }
    }

    private String buildFullLink(BorrowerProfile borrowerProfile, String prefix) {
        return prefix + "applications/" + borrowerProfile.getPartnerApplication().getId() + "/borrowers/" + borrowerProfile.getId();
    }
}
