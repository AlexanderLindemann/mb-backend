package pro.mbroker.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.mbroker.app.entity.BorrowerProfile;
import pro.mbroker.app.entity.UrlMappings;
import pro.mbroker.app.repository.BorrowerProfileRepository;
import pro.mbroker.app.service.LinkService;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final BorrowerProfileRepository borrowerProfileRepository;

    @Override
    @Transactional
    public void addLinksByProfiles(List<BorrowerProfile> borrowerProfiles, String prefixLink) {
        for (BorrowerProfile borrowerProfile : borrowerProfiles) {
            if (borrowerProfile.getLink() == null) {
                UrlMappings linkTable = createLinkTableForBorrowerProfile(borrowerProfile, prefixLink);
                borrowerProfile.setLink(linkTable);
            }
        }
        borrowerProfileRepository.saveAll(borrowerProfiles);
    }

    private UrlMappings createLinkTableForBorrowerProfile(BorrowerProfile borrowerProfile, String prefixLink) {
        UrlMappings urlMappings = new UrlMappings();
        urlMappings.setShortLink(generateRandomString(prefixLink));
        urlMappings.setFullLink(buildFullLink(borrowerProfile, prefixLink));
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

    private String buildFullLink(BorrowerProfile borrowerProfile, String prefix) {
        return prefix + "applications/" + borrowerProfile.getPartnerApplication().getId() + "/borrowers/" + borrowerProfile.getId();
    }
}
