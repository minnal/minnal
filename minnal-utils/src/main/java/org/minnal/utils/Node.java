/**
 * 
 */
package org.minnal.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public abstract class Node<T extends Node<T, P, V>, P extends Node<T, P, V>.NodePath, V> {

	private T parent;
	
	private V value;
	
	private LinkedList<T> children = new LinkedList<T>();
	
	private static final Logger logger = LoggerFactory.getLogger(Node.class);
	
	public Node(V value) {
		this.value = value;
	}
	
	public V getValue() {
		return value;
	}
	
	/**
	 * Checks if the value is already visited in the tree. Override this method to handle cycles
	 * 
	 * @param value
	 * @return
	 */
	protected boolean visited(T node) {
		return false;
	}
	
	private boolean checkVisited(T node, T child) {
		logger.debug("Checking if the node {} is visited by {}", child, node);
		if (node == null) {
			return false;
		}
		if (node.visited(child)) {
			logger.debug("Node {} is visited by {}", child, node);
			return true;
		}
		if (node.getParent() != null) {
			logger.debug("Checking if the node {} is visited by the parent of {}", child, node);
			return checkVisited(node.getParent(), child);
		}
		return false;
	}
	
	/**
	 * Marks the value as visited in the tree. Override this method to handle cycles
	 * 
	 * @param value
	 */
	protected void markVisited(T node) {
	}
	
	protected abstract T getThis();
	
	public T addChild(T child) {
		logger.debug("Attempting to add the child {} to the node {}", child, getThis());
		return addChild(child, false);
	}
	
	public T addChild(T child, boolean first) {
		if (parent == null) {
			logger.debug("Marking the current node {} as visited as this is the root", getThis());
			markVisited(getThis());
		}
		if (checkVisited(getThis(), child)) {
			logger.debug("Node {} is already visited by this node {} or one of its ancestors", child, getThis());
			return null;
		}
		child.setParent(getThis());
		if (first) {
			children.addFirst(child);
		} else {
			children.addLast(child);
		}
		markVisited(child);
		return child;
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	public boolean hasNode() {
		return false;
	}
	
	/**
	 * Does a DFT starting from this node. On every node visit calls the visitor with the node being visited
	 * 
	 * @param visitor
	 */
	public void traverse(Visitor<T> visitor) {
		traverse(getThis(), visitor);
	}
	
	/**
	 * Recursive implementation of DFT. On every node visit calls the visitor with the node being visited
	 * 
	 * @param root
	 * @param visitor
	 */
	private void traverse(T root, Visitor<T> visitor) {
		for (T child : root.getChildren()) {
			traverse(child, visitor);
		}
		visitor.visit(root);
	}
	
	/**
	 * Traverses all the available paths.
	 * 
	 * @param visitor
	 */
	public void traverse(PathVisitor<P, T> visitor) {
		LinkedList<T> list = new LinkedList<T>();
		Stack<T> stack = new Stack<T>();
		stack.add(getThis());
		T node = null;
		P path = null;
		while(! stack.isEmpty()) {
			node = stack.pop();
			list.add(node);
			path = createNodePath(copy(list));
			visitor.visit(path);
			if (node.hasChildren()) {
				for (T child : node.getChildren()) {
					stack.push(child);
				}
			} else {
				// Remove the last element if it doesn't have children or if it is not the parent of the top most element in the stack
				while(! list.isEmpty() && (! list.getLast().hasChildren() ||  (!stack.isEmpty() && ! stack.peek().getParent().equals(list.getLast())))) {
					list.removeLast();
				}
			}
		}
	}
	
	protected abstract P createNodePath(List<T> path);
	
	/**
	 * @return the parent
	 */
	public T getParent() {
		return parent;
	}
	
	/**
	 * @param parent
	 */
	public void setParent(T parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public LinkedList<T> getChildren() {
		return children;
	}
	
	private List<T> copy(List<T> list) {
		return new ArrayList<T>(list);
	}
	
	public interface Visitor<T> {
	
		void visit(T node);
	}
	
	public interface PathVisitor<P, T> {
		
		void visit(P path);
	}
	
	public class NodePath implements Iterable<T> {
		
		private List<T> path;
		
		public NodePath(List<T> path) {
			this.path = path;
		}

		public Iterator<T> iterator() {
			return path.iterator();
		}
		
		public int size() {
			return path.size();
		}
	
		public T get(int index) {
			return path.get(index);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NodePath other = (NodePath) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (path == null) {
				if (other.path != null)
					return false;
			} else if (!path.equals(other.path))
				return false;
			return true;
		}

		private Node getOuterType() {
			return Node.this;
		}
	}
	
}
