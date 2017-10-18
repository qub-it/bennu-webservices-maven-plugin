/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class WSSecurityClientHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String WSSE_NS_PREFIX = "wss";
    private static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    private static final String WSU_NS_PREFIX = "wsu";

    private static final SimpleDateFormat TIMESTAMP_FORMATER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");

    static {
        TIMESTAMP_FORMATER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private String username;
    private String password;

    public WSSecurityClientHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void close(MessageContext arg0) {

    }

    public boolean handleFault(SOAPMessageContext arg0) {
        return false;
    }

    public Set<QName> getHeaders() {
        return Collections.singleton(new QName(WSSE_NS, "Security"));
    }

    public boolean handleMessage(SOAPMessageContext smc) {

        boolean isOutbound = (Boolean) smc.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (!isOutbound) {
            return true;
        }

        try {

            final SOAPEnvelope envelope = smc.getMessage().getSOAPPart().getEnvelope();
            final SOAPFactory soapFactory = SOAPFactory.newInstance();

            // WSSecurity <Security> header
            final SOAPElement wsSecHeaderElm = soapFactory.createElement("Security", WSSE_NS_PREFIX, WSSE_NS);
            final SOAPElement userNameTokenElm = soapFactory.createElement("UsernameToken", WSSE_NS_PREFIX, WSSE_NS);

            // Username
            final SOAPElement userNameElm = soapFactory.createElement("Username", WSSE_NS_PREFIX, WSSE_NS);
            userNameElm.addTextNode(username);

            // Password
            final SOAPElement passwdElm = soapFactory.createElement("Password", WSSE_NS_PREFIX, WSSE_NS);
            passwdElm.addTextNode(password);

            // Nonce
            final SOAPElement nonceElm = soapFactory.createElement("Nonce", WSSE_NS_PREFIX, WSSE_NS);
            nonceElm.addTextNode(generateNonce());

            // Created
            final SOAPElement createdElm = soapFactory.createElement("Created", WSU_NS_PREFIX, WSU_NS);
            createdElm.addTextNode(getTimestamp());

            userNameTokenElm.addChildElement(userNameElm);
            userNameTokenElm.addChildElement(passwdElm);
            userNameTokenElm.addChildElement(nonceElm);
            userNameTokenElm.addChildElement(createdElm);

            // add child elements to the root element
            wsSecHeaderElm.addChildElement(userNameTokenElm);

            // create SOAPHeader instance for SOAP envelope if does not exist
            final SOAPHeader header = envelope.getHeader() == null ? envelope.addHeader() : envelope.getHeader();

            // add SOAP element for header to SOAP header object
            header.addChildElement(wsSecHeaderElm);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Problems in the " + getClass().getName(), e);
        }

    }

    private String generateNonce() throws NoSuchAlgorithmException {
        return Long.toHexString(SecureRandom.getInstance("SHA1PRNG").nextLong());
    }

    private String getTimestamp() throws ParseException {
        return TIMESTAMP_FORMATER.format(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
    }

}
