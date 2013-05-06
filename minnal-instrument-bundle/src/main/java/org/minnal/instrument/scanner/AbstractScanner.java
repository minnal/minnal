/**
 * 
 */
package org.minnal.instrument.scanner;

import java.io.IOException;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractScanner implements Scanner<Class<?>> {
	
	private String[] packages;
	
	private ClassLoader classLoader;
	
	public AbstractScanner(String... packages) {
		this(Thread.currentThread().getContextClassLoader(), packages);
	}
	
	public AbstractScanner(ClassLoader classLoader, String... packages) {
		this.packages = packages;
		this.classLoader = classLoader;
	}

	public void scan(Listener<Class<?>> listener) {
		try {
			ClassPath path = ClassPath.from(classLoader);
			for (String packageName : packages) {
				for (ClassInfo classInfo : path.getTopLevelClassesRecursive(packageName)) {
					Class<?> clazz = classInfo.load();
					if (match(clazz)) {
						listener.handle(clazz);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO Handle exception
		}
	}
	
	protected abstract boolean match(Class<?> clazz);
}
