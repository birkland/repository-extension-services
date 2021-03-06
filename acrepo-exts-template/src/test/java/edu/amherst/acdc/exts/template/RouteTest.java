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
package edu.amherst.acdc.exts.template;

import java.util.Dictionary;
import java.util.Map;

import edu.amherst.acdc.services.jsonld.JsonLdService;
import edu.amherst.acdc.services.jsonld.JsonLdServiceImpl;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.camel.util.KeyValueHolder;

import org.junit.Test;

/**
 * @author acoburn
 */
public class RouteTest extends CamelBlueprintTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:template")
    protected ProducerTemplate template;

    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/blueprint.xml";
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    public boolean isUseRouteBuilder() {
        return false;
    }

    @Override
    protected void addServicesOnStartup(final Map<String, KeyValueHolder<Object, Dictionary>> services) {
      services.put(JsonLdService.class.getName(), asService(new JsonLdServiceImpl(),
            "osgi.jndi.service.name", "acrepo/JsonLD"));
    }

    @Test
    public void testRoute() throws Exception {

        context.getRouteDefinition("TemplateRoute").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveAddLast().to("mock:result");
            }
        });

        context.start();

        resultEndpoint.expectedMinimumMessageCount(1);
        resultEndpoint.expectedHeaderReceived("Content-Type", "text/html");
        resultEndpoint.allMessages().body().contains("Fedora Template Service: Foo");
        resultEndpoint.allMessages().body().contains("<p>sample description</p>");

        template.sendBody("{\"title\" : \"Foo\", \"description\" : \"sample description\"}");

        // assert expectations
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRouteCustomTemplate() throws Exception {

        context.getRouteDefinition("TemplateRoute").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveAddLast().to("mock:result");
            }
        });

        context.start();

        resultEndpoint.expectedMinimumMessageCount(1);
        resultEndpoint.expectedHeaderReceived("Content-Type", "text/html");
        resultEndpoint.allMessages().body().contains("Fedora Template Service: Foo");
        resultEndpoint.allMessages().body().contains("Custom Template: Sometime 2016");
        resultEndpoint.allMessages().body().contains("<p>sample description</p>");

        template.sendBodyAndHeader(
            "{\"title\" : \"Foo\", \"description\" : \"sample description\", \"date\" : \"Sometime 2016\"}",
            "templateUri", "/edu/amherst/acdc/exts/template/template2.mustache");

        // assert expectations
        assertMockEndpointsSatisfied();
    }
}
