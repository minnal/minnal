/**
 * 
 */
package org.minnal.instrument.resource;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.minnal.core.route.RoutePattern;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;

import javassist.CtClass;

/**
 * @author ganeshs
 *
 */
public class ReadMethodCreator extends MethodCreator {
	
	public ReadMethodCreator(CtClass ctClass, EntityNodePath path) {
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
		
		while(iterator.hasNext()) {
			node = iterator.next();
			template = parent == null ? FIND_ENTITY_TEMPLATE : FIND_COLLECTION_ITEM_TEMPLATE;
			param = paramNames.get(i++);
			resolveTemplate(writer, template, node, param, parent);
			parent = node.getName();
		}
		writer.append("return ").append(node.getName()).append(";");
		return writer.toString();
	}

	@Override
	public String getMethodName() {
		return "read" + getPath().getName();
	}

}
