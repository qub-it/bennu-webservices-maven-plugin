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

package com.qubit.solution.fenixedu.bennu.webservices.servlet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.core.domain.Bennu;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceAuthenticationLevel;
import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceClientConfiguration;
import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceConfiguration;
import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceExecutionContext;
import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceServerConfiguration;
import com.qubit.solution.fenixedu.bennu.webservices.services.client.BennuWebServiceClient;
import com.qubit.solution.fenixedu.bennu.webservices.services.server.BennuWebService;

@WebListener
public class BennuWebservicesInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        synchorizeServices();

    }

    @Atomic(mode = TxMode.READ)
    private void synchorizeServices() {
        for (Class clazz : BennuWebService.getAvailableWebServices()) {
            checkServerConfigurationAndCreateIfNeeded(clazz.getName());
        }

        // Since clients are initialised by the WsServlet we need to look them up via classpath scanning
        // using spring componentprovider since we do not have OMNIS lookup available
        // 21 April 2015 - Paulo Abrantes

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(BennuWebServiceClient.class));

        Set<BeanDefinition> components =
                provider.findCandidateComponents(BennuWebServiceClient.class.getPackage().getName().replace('.', '/'));
        for (BeanDefinition component : components) {
            try {
                Class cls = Class.forName(component.getBeanClassName());
                checkClientConfigurationAndCreateIfNeeded(cls.getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // use class cls found
        }

        deleteConfigurations(Bennu.getInstance().getWebserviceConfigurationsSet().stream()
                .filter(configuration -> !configuration.isImplementationClassAvailable()).collect(Collectors.toList()));

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Atomic
    private void deleteConfigurations(List<WebServiceConfiguration> configurationsToDelete) {
        for (WebServiceConfiguration configuration : configurationsToDelete) {
            configuration.delete();
        }
    }

    @Atomic
    private void checkServerConfigurationAndCreateIfNeeded(String implementationClass) {
        WebServiceServerConfiguration readByImplementationClass =
                WebServiceConfiguration.readByImplementationClass(implementationClass);
        if (readByImplementationClass == null) {
            readByImplementationClass = new WebServiceServerConfiguration(implementationClass);
        }
        if (readByImplementationClass.getAuthenticationLevel() == WebServiceAuthenticationLevel.CUSTOM) {
            readByImplementationClass.setAuthenticationLevel(WebServiceAuthenticationLevel.WS_SECURITY_CUSTOM);
        }
        if (readByImplementationClass.getAuthenticationLevel() == WebServiceAuthenticationLevel.PASSWORD) {
            readByImplementationClass.setAuthenticationLevel(WebServiceAuthenticationLevel.WS_SECURITY);
        }
    }

    @Atomic
    private void checkClientConfigurationAndCreateIfNeeded(String implementationClass) {
        WebServiceClientConfiguration readByImplementationClass =
                WebServiceConfiguration.readByImplementationClass(implementationClass);
        if (readByImplementationClass == null) {
            readByImplementationClass = new WebServiceClientConfiguration(implementationClass);
        }
        if (readByImplementationClass.getAuthenticationLevel() == WebServiceAuthenticationLevel.PASSWORD) {
            readByImplementationClass.setAuthenticationLevel(WebServiceAuthenticationLevel.WS_SECURITY);
        }
        
        if(readByImplementationClass.getExecutionContext() == null) {
            readByImplementationClass.setExecutionContext(WebServiceExecutionContext.PRODUCTION);
        }
    }

}