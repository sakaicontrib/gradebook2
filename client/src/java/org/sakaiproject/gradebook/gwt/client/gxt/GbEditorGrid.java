package org.sakaiproject.gradebook.gwt.client.gxt;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;

public class GbEditorGrid<M extends ModelData> extends EditorGrid<M> {

	public GbEditorGrid(ListStore<M> store, ColumnModel cm) {
		super(store, cm);

	}
	
	public GbCell doWalkCells(int row, int col, int step, GbGridCallback callback,
		      boolean acceptNavs) {
		    boolean first = true;
		    int clen = cm.getColumnCount();
		    int rlen = store.getCount();
		    if (step < 0) {
		      if (col < 0) {
		        row--;
		        first = false;
		      }
		      while (row >= 0) {
		        if (!first) {
		          col = clen - 1;
		        }
		        first = false;
		        while (col >= 0) {
		          if (callback.isSelectable(row, col, acceptNavs)) {
		            return new GbCell(row, col);
		          }
		          col--;
		        }
		        row--;
		      }
		    } else {
		      if (col >= clen) {
		        row++;
		        first = false;
		      }
		      while (row < rlen) {
		        if (!first) {
		          col = 0;
		        }
		        first = false;
		        while (col < clen) {
		          if (callback.isSelectable(row, col, acceptNavs)) {
		            return new GbCell(row, col);
		          }
		          col++;
		        }
		        row++;
		      }
		    }
		    return null;
		  }

	public class GbCell {
		public int row;
		public int cell;

		public GbCell(int row, int cell) {
			this.row = row;
			this.cell = cell;
		}
	}

}
