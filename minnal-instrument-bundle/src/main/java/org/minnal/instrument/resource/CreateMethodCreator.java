/**
 * 
 */
package org.minnal.instrument.resource;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.javalite.common.Inflector;
import org.minnal.core.route.RoutePattern;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;

import javassist.CtClass;

/**
 * @author ganeshs
 *
 */
public class CreateMethodCreator extends MethodCreator {
	
	private static final String CREATE_ENTITY_TEMPLATE = ":model_class :field_name = (:model_class) request.getContentAs(:model_class.class);" +
			":field_name.persist();";
	
	private static final String CREATE_COLLECTION_ITEM_TEMPLATE = ":model_class :field_name = (:model_class) request.getContentAs(:model_class.class);" +
			":parent.collection(\":resource_name\").add(:field_name); :parent.persist();";
	
	public CreateMethodCreator(CtClass ctClass, EntityNodePath path) {
		super(ctClass, path);
	}

	@Override
	protected String createBody() {
		StringWriter writer = new StringWriter();
		Iterator<EntityNode> iterator = getPath().iterator();
		String parent = null;
		List<String> paramNames = new RoutePattern(getPath().getSinglePath()).getParameterNames();
		int i = 0;
		EntityNode node = null;
		String param = null;
		String template = null;
		
		if (getPath().size() > 1) {
			while(iterator.hasNext()) {
				template = null;
				node = iterator.next();
				if (! iterator.hasNext()) {
					template = CREATE_COLLECTION_ITEM_TEMPLATE;
					param = null;
				} else {
					template = parent == null ? FIND_ENTITY_TEMPLATE : FIND_COLLECTION_ITEM_TEMPLATE;
					param = paramNames.get(i++);
				}
				resolveTemplate(writer, template, node, param, parent);
				parent = node.getName();
			}
		} else {
			template = CREATE_ENTITY_TEMPLATE;
			node = iterator.next();
			resolveTemplate(writer, template, node, param, parent);
		}
		writer.append("return ").append(node.getName()).append(";");
		return writer.toString();
	}

	@Override
	public String getMethodName() {
		return "create" + Inflector.capitalize(getPath().getName());
	}

}
