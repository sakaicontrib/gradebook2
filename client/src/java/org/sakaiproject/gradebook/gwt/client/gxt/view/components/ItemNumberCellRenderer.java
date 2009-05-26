package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.widget.table.NumberCellRenderer;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.i18n.client.NumberFormat;

public class ItemNumberCellRenderer extends NumberCellRenderer<TreeItem> {

	public ItemNumberCellRenderer(NumberFormat format) {
		super(format);
	}
	
	@Override
	public String render(final TreeItem item, String property, final Object value) {
		String prefix = "";
		String result = null;
		final ItemModel itemModel = (ItemModel)item.getModel();
		
		if (itemModel != null && itemModel.getItemType() != null) {
			boolean isItem = itemModel.getItemType() == Type.ITEM;
			boolean isCategory = itemModel.getItemType() == Type.CATEGORY;
			boolean isGradebook = !isItem && !isCategory;
			boolean isPercentCategory = property.equals(ItemModel.Key.PERCENT_CATEGORY.name());
			boolean isPercentGrade = property.equals(ItemModel.Key.PERCENT_COURSE_GRADE.name());
			boolean isPoints = property.equals(ItemModel.Key.POINTS.name());
			
			if (isGradebook && isPercentCategory)
				return "-";
				
			if (value == null)
				return null;
			
			boolean isName = property.equals(ItemModel.Key.NAME.name());
			
			boolean isIncluded = itemModel.getIncluded() != null && itemModel.getIncluded().booleanValue();				
			boolean isTooBig = (isPercentCategory || isPercentGrade) 
				&& ((Double)value).doubleValue() > 100.00001d;
			boolean isTooSmall = ((isPercentCategory && isCategory) || (isPercentGrade && isGradebook)) && ((Double)value).doubleValue() < 99.9994d;
			
			result = super.render(item, property, value);
			
			StringBuilder cssClasses = new StringBuilder();
			
			if (!isIncluded && (isItem || isCategory))
				cssClasses.append("gbNotIncluded");
			
			if (!isItem) 
				cssClasses.append(" gbCellStrong");
			
			if (isTooBig || isTooSmall)
				cssClasses.append(" gbCellError");
				
			boolean isExtraCredit = itemModel.getExtraCredit() != null && itemModel.getExtraCredit().booleanValue();
			if (isExtraCredit) {
				
				if (isPercentGrade || (isPercentCategory && isItem) || isPoints) {
					cssClasses.append(" gbCellExtraCredit");
					prefix = "+ ";
				}

			}
			
			StringBuilder builder = new StringBuilder().append("<span class=\"").append(cssClasses.toString())
				.append("\">").append(prefix).append(result).append("</span>");
			
			if ((isCategory && isPercentCategory) || (isGradebook && isPercentGrade)) {
				builder.append(" / 100");
			} 
				
			
			return builder.toString();
		
		}
		return "";
	}

}
