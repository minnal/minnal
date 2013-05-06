/**
 * 
 */
package org.minnal.instrument;

import java.lang.management.ManagementFactory;
import java.security.CodeSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.VirtualMachine;

/**
 * @author ganeshs
 *
 */
public class MinnalAgentLoader {

	static final Logger logger = LoggerFactory.getLogger(MinnalAgentLoader.class);

    public static void loadAgent(Class<?> clazz) {
        logger.info("dynamically loading javaagent");
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);
        
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
            vm.loadAgent(codeSource.getLocation().toURI().getPath(), "");
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
