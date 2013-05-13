/**
 * 
 */
package org.minnal.instrument.resource;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javassist.CtClass;

import org.minnal.core.route.RoutePattern;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.util.DynaBean;

/**
 * @author ganeshs
 *
 */
public class UpdateMethodCreator extends MethodCreator {
	
	private static final String UPDATE_ENTITY_TEMPLATE = DynaBean.class.getName() + " dynaBean = request.getContentAs(" + DynaBean.class.getName() + ".class);" +
			":parent.updateAttributes(dynaBean.getAttributes());";
	
	public UpdateMethodCreator(CtClass ctClass, EntityNodePath path) {
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
		resolveTemplate(writer, UPDATE_ENTITY_TEMPLATE, node, param, parent);
		return writer.toString();
	}

	@Override
	public String getMethodName() {
		return "update" + getPath().getName();
	}
	
	@Override
	protected boolean returnVoid() {
		return true;
	}

}
