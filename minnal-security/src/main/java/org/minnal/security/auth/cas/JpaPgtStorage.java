/**
 * 
 */
package org.minnal.security.auth.cas;

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * @author ganeshs
 *
 */
public class JpaPgtStorage extends AbstractPgtTicketStorage implements ProxyGrantingTicketStorage {
	
	private static Logger logger = LoggerFactory.getLogger(JpaPgtStorage.class);
	
	public JpaPgtStorage() {
	}

	public void save(String proxyGrantingTicketIou, String proxyGrantingTicket) {
		logger.debug("Saving the proxy granting ticket with PGTIOU {} / PGT {}", proxyGrantingTicketIou, proxyGrantingTicket);
		if (!Strings.isNullOrEmpty(proxyGrantingTicketIou) && !Strings.isNullOrEmpty(proxyGrantingTicket)) {
			CasPgtIou pgtIou = new CasPgtIou(proxyGrantingTicket, proxyGrantingTicketIou);
			pgtIou.persist();
		}
	}

	public String retrieve(String proxyGrantingTicketIou) {
		if (Strings.isNullOrEmpty(proxyGrantingTicketIou)) {
			return null;
		}
		CasPgtIou pgtIou = CasPgtIou.first("iou", proxyGrantingTicketIou);
		if (pgtIou == null) {
			logger.info("No PGT Found for the PGTIOU - " + proxyGrantingTicketIou);
			return null;
		}
		pgtIou.delete();
		return pgtIou.getPgt();
	}

	public void cleanUp() {
		CasPgtIou.deleteAll();
	}
}
