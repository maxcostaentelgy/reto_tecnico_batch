package com.bbva.p25r.lib.r001;

import java.util.Map;

/**
 * The  interface P25RR001 class...
 */
public interface P25RR001 {

	/**
	 * The execute method...
	 */

	Integer countDocumentsToUpdate(String fechaEntrada);

	Map<String, Integer> executeUpdateProcess(String fechaEntrada);
}