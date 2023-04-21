package pro.mbroker.app.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import pro.mbroker.app.util.TokenExtractor;

@RunWith(SpringRunner.class)
public class TokenParserTest {

    @Test
    public void testTokenParser() {
        int extractSdCurrentOrganizationId = TokenExtractor.extractSdCurrentOrganizationId("eyJhbGciOiJSUzM4NCJ9.eyJzdWIiOiJpbnRfMjk1NiIsIm5iZiI6MTY4MTczMTEyOSwiZGF0YSI6eyJzZF9jdXJyZW50X2NvbnRyYWN0X25hbWUiOiJEZWZhdWx0Iiwic2RfZmVhdHVyZV9mbGFncyI6Ik1PUlRHQUdFX0FHUkVFTUVOVCxURVNUIiwic2RfYXZhdGFyX2lkIjoiMTAzMTkzODkiLCJzZF9wYXRyb255bWljIjoi0JDQu9C10LrRgdCw0L3QtNGA0L7QstC40YciLCJzZF9sYXN0X25hbWUiOiLQkNC_0L7RgdGC0L7QuyIsInNkX29yZ2FuaXphdGlvbl9uYW1lIjoi0J7QntCeINCi0LXRgdGC0YsiLCJzZF9pZCI6IjI5NTYiLCJhbGxvd2VkX2lwIjpudWxsLCJzZF9maXJzdF9uYW1lIjoi0JDQu9C10LrRgdCw0L3QtNGAIiwic2Rfc2lnbmF0dXJlX3Byb3ZpZGVycyI6IlNJR05NRSxFX1NJR05BVFVSRSxJTkZPVEVYLFNPVkNPTUJBTksiLCJzZF9jdXJyZW50X29yZ2FuaXphdGlvbl9pZCI6IjI2MzMiLCJzZF9jdXJyZW50X2NvbnRyYWN0X2lkIjoiMzU4OCIsInNkX2N1cnJlbnRfb3JnYW5pemF0aW9uX25hbWUiOiLQntCe0J4g0KLQtdGB0YLRiyIsInNkX2FwcGVhbF9pbnRlcmZhY2UiOiJCT1RIIiwic2RfZW1haWwiOiJhLmFwb3N0b2xAcHJhY3R1cy5ydSIsInNkX2N1cnJlbnRfb3JnYW5pemF0aW9uX3Blcm1pc3Npb25zIjoiVVBEQVRFX1NUQVRFTUVOVF9BTlksUkVBTF9FU1RBVEVfQU5EX0NBREFTVFJBTF9OVU1CRVJfU0VBUkNILE1PTklUT1JJTkcsQURNSU5JU1RSQVRJT05fT1JHQU5JWkFUSU9OU19SRUFELFRSQURFX0RFTEVURV9PV04sQ1JFQVRFX0VYVFJBQ1RJT05fRERVLFNZU1RFTV9BRE1JTklTVFJBVE9SLERPQ1VNRU5UX1JFUVVFU1RTX09SR0FOSVpBVElPTixFU0lHTl9ERUxJVkVSWV9CWV9DVVJSSUVSLFRSQURFX0NSRUFURSxJTlZJVEVfVVNFUl9NQU5BR0VSLERPQ1VNRU5UX1JFUVVFU1RTLEdFVF9TVEFURU1FTlQsQURNSU5JU1RSQVRJT05fQVBQX0NPTkZJR1VSQVRJT04sU0lHTkFUVVJFX0lERU5USUZJQ0FUSU9OX1RZUEVfU0VMRUNULFRSQURFX0FDQ0VTUyxTVUJTQ1JJQkVfUkVRVUVTVCxHRVRfTU9SVEdBR0VfTElTVCxNQU5BR0VfT1JHQU5JWkFUSU9OX0xJU1QsR0VUX1NUQVRFTUVOVF9SRVNVTFQsU0lHTl9HT1NfS0VZLENSRUFURV9FWFRSQUNUSU9OX1JJR0hUX01PVkVNRU5ULEVET0NfREtQX0VESVRfQUxMX0xLLE1BS0VfU1RBVEVNRU5UX09OX0FHUkVFTUVOVCxERVZFTE9QTUVOVCxFRE9DX0RLUF9FRElUX09XTixFRE9DX0RLUF9SRUFELE1BTkFHRV9VU0VSX0xJU1QsRElDVElPTkFSWV9QRVJTT05fQURNSU5JU1RSQVRJT04sQ1JFQVRFX0VYVFJBQ1RJT05fUklHSFRTLE1BS0VfU1RBVEVNRU5UX0FHUkVFRCxNQU5BR0VfVVNFUl9ET0NVTUVOVFMsU0VORF9TVEFURU1FTlQsVFJBREVfQUNDRVBULFRSQURFX0lOVklURV9BU1NJR04sR0VUX1NUQVRFTUVOVF9MSVNULFNZU1RFTV9TVVBQT1JUX0xPR0lOX0FTLFBBWSxTRVRUSU5HX0FMTE9XRURfSVAsQ0hFQ0tfU0lHTkFUVVJFLEdFVF9VU0VSX0xJU1QsR0VUX1NUQVRJU1RJQ1MsQ1JFQVRFX1NJR05BVFVSRV9ORVcsQURNSU5JU1RSQVRJT05fTk9USUZJQ0FUSU9OLFVTRV9TSUdOQVRVUkUsREVMRVRFX1NUQVRFTUVOVF9GT1JFSUdOLE1BTkFHRV9HUk9VUF9QRVJNSVNTSU9OX0xJU1QsR0VUX0dST1VQX1BFUk1JU1NJT05fTElTVCxVUERBVEVfU1RBVEVNRU5UX09XTixFRElUX09XTl9QUk9GSUxFLEFETUlOSVNUUkFUSU9OX0ZJTEVTLENSRUFURV9TSUdOQVRVUkVfRFJBRlRfT1JfRVhJU1RFTlQsR0VUX09SR0FOSVpBVElPTl9MSVNULEVMRUNUUk9OSUNfRE9DVU1FTlRfQUxMT1dFRCxHRVRfU0lHTkFUVVJFX0xJU1QsREVMRVRFX1NUQVRFTUVOVF9PV04sR0VUX0VYVFJBQ1RJT05fTElTVCxWSVNVQUxJWkVfU0lHTkFUVVJFLFVQREFURV9TVEFURU1FTlRfRk9SRUlHTixET0NVTUVOVF9TRU5EX0lOVEVSTkFMIiwic2Rfb3JncyI6IjI2NDQsMjYzMyJ9LCJzY29wZSI6WyJVUERBVEVfU1RBVEVNRU5UX0FOWSIsIlJFQUxfRVNUQVRFX0FORF9DQURBU1RSQUxfTlVNQkVSX1NFQVJDSCIsIk1PTklUT1JJTkciLCJBRE1JTklTVFJBVElPTl9PUkdBTklaQVRJT05TX1JFQUQiLCJUUkFERV9ERUxFVEVfT1dOIiwiQ1JFQVRFX0VYVFJBQ1RJT05fRERVIiwiU1lTVEVNX0FETUlOSVNUUkFUT1IiLCJET0NVTUVOVF9SRVFVRVNUU19PUkdBTklaQVRJT04iLCJFU0lHTl9ERUxJVkVSWV9CWV9DVVJSSUVSIiwiVFJBREVfQ1JFQVRFIiwiSU5WSVRFX1VTRVJfTUFOQUdFUiIsIkRPQ1VNRU5UX1JFUVVFU1RTIiwiR0VUX1NUQVRFTUVOVCIsIkFETUlOSVNUUkFUSU9OX0FQUF9DT05GSUdVUkFUSU9OIiwiU0lHTkFUVVJFX0lERU5USUZJQ0FUSU9OX1RZUEVfU0VMRUNUIiwiVFJBREVfQUNDRVNTIiwiU1VCU0NSSUJFX1JFUVVFU1QiLCJHRVRfTU9SVEdBR0VfTElTVCIsIk1BTkFHRV9PUkdBTklaQVRJT05fTElTVCIsIkdFVF9TVEFURU1FTlRfUkVTVUxUIiwiU0lHTl9HT1NfS0VZIiwiQ1JFQVRFX0VYVFJBQ1RJT05fUklHSFRfTU9WRU1FTlQiLCJFRE9DX0RLUF9FRElUX0FMTF9MSyIsIk1BS0VfU1RBVEVNRU5UX09OX0FHUkVFTUVOVCIsIkRFVkVMT1BNRU5UIiwiRURPQ19ES1BfRURJVF9PV04iLCJFRE9DX0RLUF9SRUFEIiwiTUFOQUdFX1VTRVJfTElTVCIsIkRJQ1RJT05BUllfUEVSU09OX0FETUlOSVNUUkFUSU9OIiwiQ1JFQVRFX0VYVFJBQ1RJT05fUklHSFRTIiwiTUFLRV9TVEFURU1FTlRfQUdSRUVEIiwiTUFOQUdFX1VTRVJfRE9DVU1FTlRTIiwiU0VORF9TVEFURU1FTlQiLCJUUkFERV9BQ0NFUFQiLCJUUkFERV9JTlZJVEVfQVNTSUdOIiwiR0VUX1NUQVRFTUVOVF9MSVNUIiwiU1lTVEVNX1NVUFBPUlRfTE9HSU5fQVMiLCJQQVkiLCJTRVRUSU5HX0FMTE9XRURfSVAiLCJDSEVDS19TSUdOQVRVUkUiLCJHRVRfVVNFUl9MSVNUIiwiR0VUX1NUQVRJU1RJQ1MiLCJDUkVBVEVfU0lHTkFUVVJFX05FVyIsIkFETUlOSVNUUkFUSU9OX05PVElGSUNBVElPTiIsIlVTRV9TSUdOQVRVUkUiLCJERUxFVEVfU1RBVEVNRU5UX0ZPUkVJR04iLCJNQU5BR0VfR1JPVVBfUEVSTUlTU0lPTl9MSVNUIiwiR0VUX0dST1VQX1BFUk1JU1NJT05fTElTVCIsIlVQREFURV9TVEFURU1FTlRfT1dOIiwiRURJVF9PV05fUFJPRklMRSIsIkFETUlOSVNUUkFUSU9OX0ZJTEVTIiwiQ1JFQVRFX1NJR05BVFVSRV9EUkFGVF9PUl9FWElTVEVOVCIsIkdFVF9PUkdBTklaQVRJT05fTElTVCIsIkVMRUNUUk9OSUNfRE9DVU1FTlRfQUxMT1dFRCIsIkdFVF9TSUdOQVRVUkVfTElTVCIsIkRFTEVURV9TVEFURU1FTlRfT1dOIiwiR0VUX0VYVFJBQ1RJT05fTElTVCIsIlZJU1VBTElaRV9TSUdOQVRVUkUiLCJVUERBVEVfU1RBVEVNRU5UX0ZPUkVJR04iLCJET0NVTUVOVF9TRU5EX0lOVEVSTkFMIl0sImlzcyI6InNkX2JhY2siLCJleHAiOjE2ODE3MzQ3MjksImlhdCI6MTY4MTczMTEyOSwianRpIjoiZWJiZjZmMjQtMzgyNC00MTBlLTlhMTgtYWY0YTM0N2VjOGE3In0.FnNRfdfxUPdYVpRr0ngzOk7oOmuEVBdqrqSVzSFAswWVcaEFttowtvfrSlcjvhoWKvONObkZ4bMA40WmB_HuiaoDndXRtW2cErRJQ0kieYukekSbMQSVoWrbu3GosZUIwWrdTU3xbicjnSh4gDtzLerqvzc_W9QUl9MgVLGF6ErylGbbYnTEzS44Wowk9KrgQncIQ8tPANp_eeTl_gthZFWqeeO3rFRwxcT6kFmeiGxwp6FA_XLOHwEnLJTEArDB_S4d6DWHdnr1udDoIM4CB3H_zweJKV5lwe9-3N6YpiWq_OjpFEMd1_YSVBcKtd9Y0eWX6VMxqFKKhuVdh9z8JQ");
        Assert.assertEquals(2633, extractSdCurrentOrganizationId);
    }


}
