package skyglass.composer.utils.query;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import skyglass.composer.utils.query.builder.FieldResolver;
import skyglass.composer.utils.query.builder.OrderField;

public class QueryOrderUtil {

	public static String applyOrder(List<OrderField> orderFields) {
		if (CollectionUtils.isEmpty(orderFields)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (OrderField orderField : orderFields) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			FieldResolver fieldResolver = orderField.getOrderField();
			if (fieldResolver.isMultiple()) {
				sb.append(applyMultipleOrder(orderField));
			} else {
				sb.append(applySingleOrder(orderField));
			}
		}
		if (!first) {
			return sb.toString();
		}
		return null;
	}

	private static String applyMultipleOrder(OrderField orderField) {
		StringBuilder sb = new StringBuilder();
		if (orderField.isString()) {
			sb.append(QueryFunctions.lowerCoalesce(orderField.getOrderField().getResolversArray()));
		} else {
			sb.append(QueryFunctions.coalesce(orderField.getOrderField().getResolversArray()));
		}
		sb.append(orderField.isDescending() ? " DESC" : " ASC");
		return sb.toString();
	}

	private static String applySingleOrder(OrderField orderField) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String fieldResolver : orderField.getOrderField().getResolvers()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			if (orderField.isString()) {
				sb.append(QueryFunctions.lower(fieldResolver));
			} else {
				sb.append(fieldResolver);
			}
			sb.append(orderField.isDescending() ? " DESC" : " ASC");
		}
		return sb.toString();
	}

}
