package com.bbva.p25r.lib.r001.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.p25r.lib.r001.P25RR001;
import com.datiobd.daas.DaasMongoConnector;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class P25RR001Abstract extends AbstractLibrary implements P25RR001 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected DaasMongoConnector daasMongoConnector = new DaasMongoConnector();


	/**
	* @param applicationConfigurationService the this.applicationConfigurationService to set
	*/
	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

}