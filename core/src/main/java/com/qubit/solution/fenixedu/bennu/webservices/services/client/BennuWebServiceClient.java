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

package com.qubit.solution.fenixedu.bennu.webservices.services.client;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qubit.solution.fenixedu.bennu.webservices.domain.webservice.WebServiceClientConfiguration;
import com.qubit.solution.fenixedu.bennu.webservices.tools.keystore.KeyStoreWorker;
import com.sun.xml.ws.developer.JAXWSProperties;

import pt.ist.fenixframework.Atomic;

public abstract class BennuWebServiceClient<T> {

//    static {
//        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
//
//            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
//                if (hostname.equals("localhost")) {
//                    return true;
//                }
//                return false;
//            }
//        });
//    }

    private static final Logger logger = LoggerFactory.getLogger(BennuWebServiceClient.class);

    private String username;
    private String password;

    public BennuWebServiceClient() {
        super();
        WebServiceClientConfiguration configuration = getWebServiceClientConfiguration();
        this.username = configuration.getClientUsername();
        this.password = configuration.getClientPassword();
    }

    public BennuWebServiceClient(String username, String password) {
        super();
        this.username = username;
        this.password = password;

        // Just to make it create the configuration is does not exists
        getWebServiceClientConfiguration();
    }

    protected void logDebug(String message) {
        logger.debug(message);
    }

    protected void logWarning(String message) {
        logger.warn(message);
    }

    protected void logInfo(String message) {
        logger.info(message);
    }

    public T getClient() {
        final WebServiceClientConfiguration webServiceClientConfiguration = getWebServiceClientConfiguration();
        if (webServiceClientConfiguration.isProductionContext() && webServiceClientConfiguration.isDevelopmentMode()) {
            throw new RuntimeException("Cannot execute webservice: not in production environment");
        }

        BindingProvider port = getService();
        setupClient(port);
        return (T) port;
    }

    protected abstract BindingProvider getService();

    protected void setupClient(BindingProvider bindingProvider) {
        WebServiceClientConfiguration configuration = getWebServiceClientConfiguration();
        String url = configuration.getUrl();

        if (url != null && url.length() > 0) {
            bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        }
        if (configuration.isSSLActive()) {
            setSSLConnection(bindingProvider);
        }

        if (configuration.isSecured()) {
            
            if (configuration.isUsingWSSecurity()) {
                List<Handler> handlerList = bindingProvider.getBinding().getHandlerChain();
                if (handlerList == null) {
                    handlerList = new ArrayList<Handler>();
                }
                handlerList.add(new WSSecurityClientHandler(this.username, this.password));
                bindingProvider.getBinding().setHandlerChain(handlerList);

            } else {
                bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, this.username);
                bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, this.password);
            }

        }
    }

    protected WebServiceClientConfiguration getWebServiceClientConfiguration() {
        WebServiceClientConfiguration configuration =
                WebServiceClientConfiguration.readByImplementationClass(getClass().getName());
        if (configuration == null) {
            configuration = createConfiguration();
        }

        return configuration;
    }

    @Atomic
    private WebServiceClientConfiguration createConfiguration() {
        return new WebServiceClientConfiguration(getClass().getName());
    }

    private void setSSLConnection(BindingProvider bp) {
        WebServiceClientConfiguration webServiceClientConfiguration = getWebServiceClientConfiguration();
        try {
            SSLContext sslContext = SSLContext.getInstance(getSSLVersion());
            KeyStoreWorker helper = webServiceClientConfiguration.getDomainKeyStore().getHelper();
            KeyManagerFactory kmf =
                    helper.getKeyManagerFactoryNeededForSSL(webServiceClientConfiguration.getAliasForSSLCertificate());
            TrustManagerFactory tmf = helper.getTrustManagerFactoryNeededForSSL();
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            bp.getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, sslContext.getSocketFactory());
        } catch (Exception e) {
            throw new RuntimeException("Problems creating sslContext", e);
        }
    }

    protected String getSSLVersion() {
        return "TLSv1.2";
    }
}
