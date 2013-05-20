/**
 * 
 */
package org.minnal.instrument.resource;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javassist.CtClass;

import org.activejpa.entity.EntityCollection;
import org.activejpa.entity.Filter;
import org.javalite.common.Inflector;
import org.minnal.core.route.RoutePattern;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;

/**
 * @author ganeshs
 *
 */
public class ListMethodCreator extends MethodCreator {
	
	private static final String LIST_ENTITY_TEMPLATE = Filter.class.getName() + " filter = " + ResourceUtil.class.getName() + ".getFilter(request, :search_params);" +
			"java.util.List :field_name = :model_class.where(filter);";
	
	private static final String LIST_COLLECTION_TEMPLATE = EntityCollection.class.getName() + " :field_nameCollection = :parent.collection(\":resource_name\");" + 
			"java.util.List :field_name = :field_nameCollection.all();";

	public ListMethodCreator(CtClass ctClass, EntityNodePath path) {
		super(ctClass, path);
	}

	@Override
	protected String createBody() {
		StringWriter writer = new StringWriter();
		Iterator<EntityNode> iterator = getPath().iterator();
		String parent = null;
		List<String> paramNames = new RoutePattern(getPath().getBulkPath()).getParameterNames();
		int i = 0;
		EntityNode node = null;
		String param = null;
		String template = null;
		
		if (getPath().size() > 1) {
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
				resolveTemplate(writer, template, node, param, parent);
				parent = node.getName();
			}
		} else {
			template = LIST_ENTITY_TEMPLATE;
			node = iterator.next();
			resolveTemplate(writer, template, node, param, parent);
		}
		
		writer.append("return ").append(node.getName()).append(";");
		return writer.toString();
	}
	
	@Override
	public String getMethodName() {
		return "list" + Inflector.capitalize(getPath().getName());
	}
}
