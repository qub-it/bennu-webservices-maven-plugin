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

package com.qubit.solution.fenixedu.ulisboa.mavenPlugins;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Mojo(name = "jaxws-merger", defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class SunJaxWSMerger extends AbstractMojo {

    @Parameter(property = "project")
    protected MavenProject mavenProject;

    @Component
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (mavenProject.getPackaging().equals("pom")) {
            getLog().info("Project is pom type. Skipping less generation");
            return;
        }

        @SuppressWarnings("unchecked")
        Set<Artifact> artifacts = mavenProject.getDependencyArtifacts();

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        }

        Document resultDocument = documentBuilder.newDocument();
        Element rootElement = resultDocument.createElement("endpoints");
        resultDocument.appendChild(rootElement);
        rootElement.setAttribute("xmlns", "http://java.sun.com/xml/ns/jax-ws/ri/runtime");
        rootElement.setAttribute("version", "2.0");

        for (Artifact artifact : artifacts) {

            if (!artifact.getType().equals("jar")) {
                continue;
            }

            File file = artifact.getFile();
            if (file == null) {
                continue;
            }
            try (JarFile jarFile = new JarFile(file)) {
                ZipEntry entry = jarFile.getEntry("META-INF/resources/WEB-INF/sun-jaxws-fragment.xml");
                if (entry != null) {
                    Document doc = documentBuilder.parse(jarFile.getInputStream(entry));
                    doc.getDocumentElement().normalize();

                    NodeList list = doc.getElementsByTagName("endpoint");
                    for (int i = 0; i < list.getLength(); i++) {
                        Node cloneNode = list.item(i).cloneNode(true);
                        resultDocument.adoptNode(cloneNode);
                        rootElement.appendChild(cloneNode);

                        if (findChildElement(cloneNode, "handler-chains") == null) {
                            Element handlerChains = resultDocument.createElement("handler-chains");
                            cloneNode.appendChild(handlerChains);
                            handlerChains.setAttribute("xmlns", "http://java.sun.com/xml/ns/javaee");
                        }

                        Element handlerChains = findChildElement(cloneNode, "handler-chains");

                        if (findChildElement(handlerChains, "handler-chain") == null) {
                            Element handlerChain = resultDocument.createElement("handler-chain");
                            handlerChains.appendChild(handlerChain);
                        }

                        Element handlerChain = findChildElement(handlerChains, "handler-chain");

                        Element handler = resultDocument.createElement("handler");
                        handlerChain.appendChild(handler);
                        Element handlerName = resultDocument.createElement("handler-name");
                        handler.appendChild(handlerName);
                        handlerName.setTextContent("SecurityHandler");
                        Element handlerClass = resultDocument.createElement("handler-class");
                        handler.appendChild(handlerClass);
                        handlerClass.setTextContent(
                                "com.qubit.solution.fenixedu.bennu.webservices.services.server.BennuWebServiceHandler");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

        }

        try {
            File directory = new File(mavenProject.getBasedir() + File.separator + "src" + File.separator + "main"
                    + File.separator + "webapp" + File.separator + "WEB-INF");
            directory.mkdirs();
            File file = new File(directory, "sun-jaxws.xml");
            file.createNewFile();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(resultDocument);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private Element findChildElement(final Node node, final String elementName) {
        final NodeList list = node.getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            Node child = list.item(i);

            if (elementName.equals(child.getNodeName())) {
                return (Element) child;
            }
        }

        return null;
    }

}