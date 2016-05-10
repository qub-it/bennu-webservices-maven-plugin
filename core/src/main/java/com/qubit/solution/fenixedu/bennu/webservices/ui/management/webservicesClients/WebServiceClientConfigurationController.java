/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: paulo.abrantes@qub-it.com
 *
 * 
 * This file is part of FenixEdu bennu-webservices.
 *
 * FenixEdu bennu-webservices is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu bennu-webservices is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu bennu-webservices.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.qubit.solution.fenixedu.bennu.webservices.ui.management.webservicesClients;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pt.ist.fenixframework.Atomic;

import com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore;
import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceAuthenticationLevel;
import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceClientConfiguration;
import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceExecutionContext;
import com.qubit.solution.fenixedu.bennu.webservices.ui.BennuWebservicesController;
import com.qubit.solution.fenixedu.bennu.webservices.ui.WebservicesBaseController;

@SpringFunctionality(app = BennuWebservicesController.class, title = "label.title.management.webservicesClients",
        accessGroup = "#managers")
@RequestMapping("/webservices/management/webservicesclients/webserviceclientconfiguration")
public class WebServiceClientConfigurationController extends WebservicesBaseController {

    @RequestMapping
    public String home(Model model) {
        return "forward:/webservices/management/webservicesclients/webserviceclientconfiguration/";
    }

    private WebServiceClientConfiguration getWebServiceClientConfiguration(Model m) {
        return (WebServiceClientConfiguration) m.asMap().get("webServiceClientConfiguration");
    }

    private void setWebServiceClientConfiguration(WebServiceClientConfiguration webServiceClientConfiguration, Model m) {
        m.addAttribute("webServiceClientConfiguration", webServiceClientConfiguration);
    }

    @RequestMapping(value = "/")
    public String search(@RequestParam(value = "implementationclass", required = false) java.lang.String implementationClass,
            @RequestParam(value = "secured", required = false) Boolean secured, Model model) {
        List<WebServiceClientConfiguration> searchwebserviceclientconfigurationResultsDataSet =
                filterSearchWebServiceClientConfiguration(implementationClass, secured);

        model.addAttribute("searchwebserviceclientconfigurationResultsDataSet", searchwebserviceclientconfigurationResultsDataSet);
        return "webservices/management/webservicesclients/webserviceclientconfiguration/search";
    }

    private List<WebServiceClientConfiguration> getSearchUniverseSearchWebServiceClientConfigurationDataSet() {
        return new ArrayList<WebServiceClientConfiguration>(WebServiceClientConfiguration.readAll());
    }

    private List<WebServiceClientConfiguration> filterSearchWebServiceClientConfiguration(java.lang.String implementationClass,
            Boolean secured) {

        return getSearchUniverseSearchWebServiceClientConfigurationDataSet()
                .stream()
                .filter(webServiceClientConfiguration -> implementationClass == null
                        || implementationClass.length() == 0
                        || (webServiceClientConfiguration.getImplementationClass() != null
                                && webServiceClientConfiguration.getImplementationClass().length() > 0 && webServiceClientConfiguration
                                .getImplementationClass().toLowerCase().contains(implementationClass.toLowerCase())))
                .filter(webServiceClientConfiguration -> secured == null || webServiceClientConfiguration.isSecured() == secured)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/search/update/{oid}")
    public String processSearchToUpdateAction(@PathVariable("oid") WebServiceClientConfiguration webServiceClientConfiguration,
            Model model) {
        return "redirect:/webservices/management/webservicesclients/webserviceclientconfiguration/update/"
                + webServiceClientConfiguration.getExternalId();
    }

    @RequestMapping(value = "/update/{oid}/entries/{selectedKeyStore}", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody List<String> requestKeyStoreEntries(
            @PathVariable("oid") WebServiceClientConfiguration webServiceClientConfiguration,
            @PathVariable("selectedKeyStore") DomainKeyStore selectedDomainKeyStore, Model model) {
        return selectedDomainKeyStore.getAvailableCertificateAlias();
    }

    @RequestMapping(value = "/update/{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") WebServiceClientConfiguration webServiceClientConfiguration, Model model) {
        model.addAttribute("WebServiceClientConfiguration_domainKeyStore_options",
                new ArrayList<com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore>(Bennu.getInstance()
                        .getDomainKeyStoresSet()));
        ArrayList<WebServiceAuthenticationLevel> values = new ArrayList<WebServiceAuthenticationLevel>();
        values.add(WebServiceAuthenticationLevel.NONE);
        values.add(WebServiceAuthenticationLevel.BASIC_AUTH);
        values.add(WebServiceAuthenticationLevel.WS_SECURITY);

        model.addAttribute("authenticationLevelValues", values);
        setWebServiceClientConfiguration(webServiceClientConfiguration, model);
        
        ArrayList<WebServiceExecutionContext> executionContextValues = new ArrayList<WebServiceExecutionContext>();
        executionContextValues.add(WebServiceExecutionContext.PRODUCTION);
        executionContextValues.add(WebServiceExecutionContext.DEVELOPMENT);
        model.addAttribute("executionContextValues", executionContextValues);
        
        
        return "webservices/management/webservicesclients/webserviceclientconfiguration/update";
    }

    @RequestMapping(value = "/update/{oid}", method = RequestMethod.POST)
    public String update(
            @PathVariable("oid") WebServiceClientConfiguration webServiceClientConfiguration,
            @RequestParam(value = "executioncontext", required = false) WebServiceExecutionContext executionContext,
            @RequestParam(value = "authenticationlevel", required = false) WebServiceAuthenticationLevel authenticationLevel,
            @RequestParam(value = "url", required = false) java.lang.String url,
            @RequestParam(value = "sslactive", required = false) boolean sslActive,
            @RequestParam(value = "domainkeystore", required = false) com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore domainKeyStore,
            @RequestParam(value = "aliasforsslcertificate", required = false) java.lang.String aliasForSSLCerficate,
            @RequestParam(value = "aliasforcertificate", required = false) java.lang.String aliasForCertificate, @RequestParam(
                    value = "clientusername", required = false) java.lang.String clientUsername, @RequestParam(
                    value = "clientpassword", required = false) java.lang.String clientPassword, Model model) {

//        if (url != null && url.length() > 0 && url.startsWith("http:") && sslActive) {
//            url = url.replace("http:", "https:");
//        }
//        if (url != null && url.length() > 0 && url.startsWith("https:") && !sslActive) {
//            url = url.replace("https:", "http:");
//        }

        setWebServiceClientConfiguration(webServiceClientConfiguration, model);
        updateWebServiceClientConfiguration(executionContext, authenticationLevel, url, sslActive, domainKeyStore, aliasForSSLCerficate,
                aliasForCertificate, clientUsername, clientPassword, model);

        return "redirect:/webservices/management/webservicesclients/webserviceclientconfiguration/";
    }

    @Atomic
    public void updateWebServiceClientConfiguration(WebServiceExecutionContext executionContext, WebServiceAuthenticationLevel authenticationLevel, java.lang.String url,
            boolean sslActive, com.qubit.solution.fenixedu.bennu.webservices.domain.keystore.DomainKeyStore domainKeyStore,
            java.lang.String aliasForSSLCertificate, java.lang.String aliasForCerficate, String clientUsername,
            String clientPassword, Model m) {

        WebServiceClientConfiguration webServiceClientConfiguration = getWebServiceClientConfiguration(m);
        webServiceClientConfiguration.setExecutionContext(executionContext);
        webServiceClientConfiguration.setAuthenticationLevel(authenticationLevel);
        webServiceClientConfiguration.setUrl(url);
        webServiceClientConfiguration.setSslActive(sslActive);
        webServiceClientConfiguration.setAliasForSSLCertificate(aliasForSSLCertificate);
        webServiceClientConfiguration.setDomainKeyStore(domainKeyStore);
        webServiceClientConfiguration.setAliasForCerficate(aliasForCerficate);
        webServiceClientConfiguration.setClientUsername(clientUsername);
        webServiceClientConfiguration.setClientPassword(clientPassword);
    }

}
