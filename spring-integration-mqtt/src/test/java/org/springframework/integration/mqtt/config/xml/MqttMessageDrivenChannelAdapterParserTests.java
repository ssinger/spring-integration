/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.mqtt.config.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.integration.test.util.TestUtils;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Gary Russell
 * @author Artem Bilan
 * @since 4.0
 *
 */
@RunWith(SpringRunner.class)
@DirtiesContext
public class MqttMessageDrivenChannelAdapterParserTests {

	@Autowired
	private MqttPahoMessageDrivenChannelAdapter noTopicsAdapter;

	@Autowired
	private MqttPahoMessageDrivenChannelAdapter noTopicsAdapterDefaultCF;

	@Autowired
	private MqttPahoMessageDrivenChannelAdapter oneTopicAdapter;

	@Autowired
	private MqttPahoMessageDrivenChannelAdapter twoTopicsAdapter;

	@Autowired
	private MqttPahoMessageDrivenChannelAdapter twoTopicsSingleQosAdapter;

	@Autowired
	private MessageChannel out;

	@Autowired
	private MqttMessageConverter converter;

	@Autowired
	private DefaultMqttPahoClientFactory clientFactory;

	@Autowired
	private MessageChannel errors;

	@Test
	public void testNoTopics() { // INT-3467 no longer required to have topics
		assertEquals("tcp://localhost:1883", TestUtils.getPropertyValue(noTopicsAdapter, "url"));
		assertFalse(TestUtils.getPropertyValue(noTopicsAdapter, "autoStartup", Boolean.class));
		assertEquals("foo", TestUtils.getPropertyValue(noTopicsAdapter, "clientId"));
		assertEquals(0, TestUtils.getPropertyValue(noTopicsAdapter, "topics", Collection.class).size());
		assertSame(out, TestUtils.getPropertyValue(noTopicsAdapter, "outputChannel"));
		assertSame(clientFactory, TestUtils.getPropertyValue(noTopicsAdapter, "clientFactory"));
		assertEquals(5000, TestUtils.getPropertyValue(this.noTopicsAdapter, "recoveryInterval"));
	}

	@Test
	public void testNoTopicsDefaultCF() { // INT-3598
		assertEquals("tcp://localhost:1883", TestUtils.getPropertyValue(noTopicsAdapterDefaultCF, "url"));
		assertFalse(TestUtils.getPropertyValue(noTopicsAdapterDefaultCF, "autoStartup", Boolean.class));
		assertEquals("foo", TestUtils.getPropertyValue(noTopicsAdapterDefaultCF, "clientId"));
		assertEquals(0, TestUtils.getPropertyValue(noTopicsAdapterDefaultCF, "topics", Collection.class).size());
		assertSame(out, TestUtils.getPropertyValue(noTopicsAdapterDefaultCF, "outputChannel"));
	}

	@Test
	public void testOneTopic() {
		assertEquals("tcp://localhost:1883", TestUtils.getPropertyValue(oneTopicAdapter, "url"));
		assertFalse(TestUtils.getPropertyValue(oneTopicAdapter, "autoStartup", Boolean.class));
		assertEquals(25, TestUtils.getPropertyValue(oneTopicAdapter, "phase"));
		assertEquals("foo", TestUtils.getPropertyValue(oneTopicAdapter, "clientId"));
		assertEquals("Topic [topic=bar, qos=1]",
				TestUtils.getPropertyValue(oneTopicAdapter, "topics", Collection.class).iterator().next().toString());
		assertSame(converter, TestUtils.getPropertyValue(oneTopicAdapter, "converter"));
		assertEquals(123L, TestUtils.getPropertyValue(oneTopicAdapter, "messagingTemplate.sendTimeout"));
		assertSame(out, TestUtils.getPropertyValue(oneTopicAdapter, "outputChannel"));
		assertSame(clientFactory, TestUtils.getPropertyValue(oneTopicAdapter, "clientFactory"));
		assertSame(errors, TestUtils.getPropertyValue(oneTopicAdapter, "errorChannel"));
	}

	@Test
	public void testTwoTopics() {
		assertEquals("tcp://localhost:1883", TestUtils.getPropertyValue(twoTopicsAdapter, "url"));
		assertFalse(TestUtils.getPropertyValue(twoTopicsAdapter, "autoStartup", Boolean.class));
		assertEquals(25, TestUtils.getPropertyValue(twoTopicsAdapter, "phase"));
		assertEquals("foo", TestUtils.getPropertyValue(twoTopicsAdapter, "clientId"));
		Iterator<?> iterator = TestUtils.getPropertyValue(twoTopicsAdapter, "topics", Collection.class).iterator();
		assertEquals("Topic [topic=bar, qos=0]", iterator.next().toString());
		assertEquals("Topic [topic=baz, qos=2]", iterator.next().toString());
		assertSame(converter, TestUtils.getPropertyValue(twoTopicsAdapter, "converter"));
		assertEquals(123L, TestUtils.getPropertyValue(twoTopicsAdapter, "messagingTemplate.sendTimeout"));
		assertSame(out, TestUtils.getPropertyValue(twoTopicsAdapter, "outputChannel"));
		assertSame(clientFactory, TestUtils.getPropertyValue(twoTopicsAdapter, "clientFactory"));
	}

	@Test
	public void testTwoTopicsSingleQos() {
		Iterator<?> iterator = TestUtils.getPropertyValue(twoTopicsSingleQosAdapter, "topics", Collection.class).iterator();
		assertEquals("Topic [topic=bar, qos=0]", iterator.next().toString());
		assertEquals("Topic [topic=baz, qos=0]", iterator.next().toString());
	}

}
