/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.gradebook.gwt.client.gxt.upload;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;

public class ClientUploadUtility {

	public static List<BeanModel> convertHeadersToBeanModels(List<ImportHeader> headers) {
		List<BeanModel> itemModels = new ArrayList<BeanModel>();
		BeanModelFactory factory = BeanModelLookup.get().getFactory(headers.get(0).getClass());
		if (factory == null) {
			throw new RuntimeException("No BeanModelFactory found for " + headers.get(0).getClass());
		}
		List<BeanModel> converted = factory.createModel(headers);
		itemModels.addAll(converted);

		return itemModels;
	}


}
