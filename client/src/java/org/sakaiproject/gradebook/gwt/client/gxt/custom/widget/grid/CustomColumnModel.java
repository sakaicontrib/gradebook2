/**********************************************************************************
*
* Copyright (c) 2008, 2009, 2010, 2011, 2012 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid;

import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

public class CustomColumnModel extends ColumnModel {

	private HashMap<String, Integer> dataIndexMap;
	
	public CustomColumnModel(List<ColumnConfig> columns) {
		super(columns);
		this.dataIndexMap = new HashMap<String, Integer>();
		
		if (columns != null) {
			for (int i=0;i<columns.size();i++) {
				dataIndexMap.put(columns.get(i).getDataIndex(), Integer.valueOf(i));
			}
		}
	}

	@Override
	public int findColumnIndex(String dataIndex) {
		if (dataIndexMap != null) {
			Integer val = dataIndexMap.get(dataIndex);
			if (val != null)
				return val.intValue();
		}
		
		return -1;
	}
	
	@Override
	public void setHidden(int colIndex, boolean hidden) {
		super.setHidden(colIndex, hidden);
	}
	
}
