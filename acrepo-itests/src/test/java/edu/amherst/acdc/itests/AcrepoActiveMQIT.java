/*
 * Copyright 2016 Amherst College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.amherst.acdc.itests;

import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.slf4j.Logger;

/**
 * @author Bethany Seeger
 * @since June 24, 2016
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AcrepoActiveMQIT extends AbstractOSGiIT {

    private static Logger LOGGER = getLogger(AcrepoActiveMQIT.class);

    @Configuration
    public Option[] config() {
        final ConfigurationManager cm = new ConfigurationManager();
        final String jmsPort = cm.getProperty("fcrepo.dynamic.jms.port");
        final String rmiRegistryPort = cm.getProperty("karaf.rmiRegistry.port");
        final String rmiServerPort = cm.getProperty("karaf.rmiServer.port");
        final String sshPort = cm.getProperty("karaf.ssh.port");
        final String brokerUrl = "tcp://localhost:" + jmsPort;

        return new Option[] {
            karafDistributionConfiguration()
                .frameworkUrl(maven().groupId("org.apache.karaf").artifactId("apache-karaf")
                        .version(cm.getProperty("karaf.version")).type("zip"))
                .unpackDirectory(new File("build", "exam"))
                .useDeployFolder(false),
            logLevel(LogLevel.INFO),
            keepRuntimeFolder(),
            configureConsole().ignoreLocalConsole(),

            features(maven().groupId("org.apache.karaf.features").artifactId("standard")
                        .versionAsInProject().classifier("features").type("xml"), "scr"),
            features(maven().groupId("org.apache.activemq").artifactId("activemq-karaf")
                        .type("xml").classifier("features").versionAsInProject()),
            features(maven().groupId("org.apache.camel.karaf").artifactId("apache-camel")
                        .type("xml").classifier("features").versionAsInProject()),
            features(maven().groupId("edu.amherst.acdc").artifactId("acrepo-karaf")
                        .type("xml").classifier("features").versionAsInProject(),
                        "acrepo-services-activemq"),

            mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpclient-osgi").versionAsInProject(),
            mavenBundle().groupId("org.apache.httpcomponents").artifactId("httpcore-osgi").versionAsInProject(),

            editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", rmiRegistryPort),
            editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", rmiServerPort),
            editConfigurationFilePut("etc/org.apache.karaf.shell.cfg", "sshPort", sshPort),
            editConfigurationFilePut("etc/edu.amherst.acdc.services.activemq.cfg", "jms.brokerUrl", brokerUrl),
       };
    }

    @Test
    public void testInstallation() throws Exception {
        assertTrue(featuresService.isInstalled(featuresService.getFeature("camel-core")));
        assertTrue(featuresService.isInstalled(featuresService.getFeature("camel-blueprint")));
        assertTrue(featuresService.isInstalled(featuresService.getFeature("activemq-camel")));
        assertTrue(featuresService.isInstalled(featuresService.getFeature("acrepo-services-activemq")));
    }
}
