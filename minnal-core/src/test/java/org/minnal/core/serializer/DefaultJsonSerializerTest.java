package org.minnal.core.serializer;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;


public class DefaultJsonSerializerTest {
		
	private DefaultJsonSerializer serializer;
	
	private DummyModel model;
	
	@BeforeMethod
	public void setup() {
		 serializer = new DefaultJsonSerializer();
		 model = new DummyModel("name","value");
	}
	
	@Test
	public void shouldSerializeModel(){
		ChannelBuffer channelBuffer = serializer.serialize(model);
		assertEquals("{\"name\":\"name\",\"value\":\"value\",\"composites\":null,\"association\":null}", 
				channelBuffer.toString(Charsets.UTF_8)); 
	}
	
	@Test
	public void shouldDeserializeModel(){
		ChannelBuffer channelBuffer = serializer.serialize(model);
		DummyModel dummyModel= serializer.deserialize(channelBuffer, DummyModel.class);
		assertEquals(dummyModel.name,model.name); 
	}
	
	@Test
	public void shouldSerializeModelWithEmptySets(){
		Set<String> includes = new HashSet<String>();
		includes.add("name");
		ChannelBuffer channelBuffer = serializer.serialize(model, new HashSet<String>(), new HashSet<String>());
		assertTrue(channelBuffer.toString(Charsets.UTF_8).contains("value")); 
	}
	
	@Test
	public void shouldSerializeModelWithIncludes(){
		Set<String> includes = new HashSet<String>();
		includes.add("name");
		ChannelBuffer channelBuffer = serializer.serialize(model, null, includes);
		assertFalse(channelBuffer.toString(Charsets.UTF_8).contains("value")); 
	}
	
	@Test
	public void shouldSerializeModelWithExcludes(){
		Set<String> excludes = new HashSet<String>();
		excludes.add("name");
		ChannelBuffer channelBuffer = serializer.serialize(model, excludes, null);
		assertFalse(channelBuffer.toString(Charsets.UTF_8).contains("name")); 
	}
	
	public DummyModel createNestedDummyModel(){
		 DummyModel assosiationModel = new DummyModel("name","value");
		 DummyModel nestedModel = new DummyModel("name","value");
		 Set<DummyModel> nestedModelSet = new HashSet<DummyModel>();
		 nestedModelSet.add(nestedModel);
		 model.setAssociation(assosiationModel);
		 model.setComposites(nestedModelSet);
		 return model;
	}
	
	@Test
	public void shouldSerializeNestedModelWithIncludes(){
		model = createNestedDummyModel();
		Set<String> includes = new HashSet<String>();
		includes.add("name");
		ChannelBuffer channelBuffer = serializer.serialize(model, null, includes);
		assertFalse(channelBuffer.toString(Charsets.UTF_8).contains("value")); 
	}
	
	@Test
	public void shouldSerializeNestedModelWithExcludes(){
		model = createNestedDummyModel();
		Set<String> excludes = new HashSet<String>();
		excludes.add("name");
		ChannelBuffer channelBuffer = serializer.serialize(model, excludes, null);
		assertFalse(channelBuffer.toString(Charsets.UTF_8).contains("name")); 
	}

	
	public static class DummyModel{
		
		private String name;
		
		private String value;
		
		private Set<DummyModel> composites;
		
		private DummyModel association;
		
		public DummyModel(){
		}
		
		public DummyModel(String name, String value){
			this.name = name;
			this.value = value;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		
		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
		
		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * @return the composites
		 */
		public Set<DummyModel> getComposites() {
			return composites;
		}

		/**
		 * @param composites the composites to set
		 */
		public void setComposites(Set<DummyModel> composites) {
			this.composites = composites;
		}

		/**
		 * @return the association
		 */
		public DummyModel getAssociation() {
			return association;
		}

		/**
		 * @param association the association to set
		 */
		public void setAssociation(DummyModel association) {
			this.association = association;
		}
	}
	
}
