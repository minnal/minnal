/**
 * 
 */
package org.minnal.instrument.resource;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javassist.CtClass;

import org.activejpa.entity.EntityCollection;
import org.minnal.core.route.RoutePattern;
import org.minnal.core.server.exception.NotFoundException;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;

/**
 * @author ganeshs
 *
 */
public class ListMethodCreator extends MethodCreator {
	
	private EntityNodePath path;

	private static final String FIND_ENTITY_TEMPLATE = ":model_class :field_name = :model_class.first(new Object[]{\":entity_key\", " +
			"request.getHeader(\":param_name\")}); if (:field_name == null) {throw new " + NotFoundException.class.getName() + 
			"(\":field_name with :entity_key \" + request.getHeader(\":param_name\") + \" not found\");}";
	
	private static final String FIND_COLLECTION_ITEM_TEMPLATE = ":model_class :field_name = :parent.collection(\":field_name\").first(new Object[]{\":entity_key\", " +
			"request.getHeader(\":param_name\")}); if (:field_name == null) {throw new " + NotFoundException.class.getName() + 
			"(\":field_name with :entity_key \" + request.getHeader(\":param_name\") + \" not found\");}";
	
	private static final String LIST_ENTITY_TEMPLATE = "java.util.List :field_name = :model_class.all();";
	
	private static final String LIST_COLLECTION_TEMPLATE = EntityCollection.class.getName() + " :field_nameCollection = :parent.collection(\":field_name\");" + 
			"java.util.List :field_name = :field_nameCollection.all();";

	public ListMethodCreator(CtClass ctClass, EntityNodePath path) {
		super(ctClass);
		this.path = path;
	}

	@Override
	protected String createBody() {
		StringWriter writer = new StringWriter();
		Iterator<EntityNode> iterator = path.iterator();
		String parent = null;
		List<String> paramNames = new RoutePattern(path.getBulkPath()).getParameterNames();
		int i = 0;
		EntityNode node = null;
		String param = null;
		String template = null;
		
		if (path.size() > 1) {
			while(iterator.hasNext()) {
				template = null;
				node = iterator.next();
				if (! iterator.hasNext()) {
					template = LIST_COLLECTION_TEMPLATE;
					param = null;
				} else {
					template = parent == null ? FIND_ENTITY_TEMPLATE : FIND_COLLECTION_ITEM_TEMPLATE;
					param = paramNames.get(i++);
				}
				addLine(writer, template, node, param, parent);
				parent = node.getName();
			}
		} else {
			template = LIST_ENTITY_TEMPLATE;
			node = iterator.next();
			addLine(writer, template, node, param, parent);
		}
		
		writer.append("return ").append(node.getName()).append(";");
		return writer.toString();
	}
	
	private void addLine(StringWriter writer, String template, EntityNode node, String paramName, String parent) {
		Map<String, String> placeholders = new HashMap<String, String>();
		placeholders.put("model_class", node.getEntityMetaData().getEntityClass().getName());
		placeholders.put("field_name", node.getName());
		placeholders.put("entity_key", node.getEntityMetaData().getEntityKey());
		placeholders.put("param_name", paramName);
		placeholders.put("parent", parent);
		
		for (Entry<String, String> entry : placeholders.entrySet()) {
			template = template.replaceAll(":" + entry.getKey(), entry.getValue());
		}
		writer.append(template);
	}

	@Override
	public String getMethodName() {
		return "list" + path.getName();
	}


}
