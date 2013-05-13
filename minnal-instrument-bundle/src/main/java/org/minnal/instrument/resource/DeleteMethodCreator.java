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
public class DeleteMethodCreator extends MethodCreator {
	
	private static final String DELETE_ENTITY_TEMPLATE = ":parent.delete();";
	
	public DeleteMethodCreator(CtClass ctClass, EntityNodePath path) {
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
		resolveTemplate(writer, DELETE_ENTITY_TEMPLATE, node, param, parent);
		return writer.toString();
	}

	@Override
	public String getMethodName() {
		return "delete" + getPath().getName();
	}
	
	@Override
	protected boolean returnVoid() {
		return true;
	}

}
