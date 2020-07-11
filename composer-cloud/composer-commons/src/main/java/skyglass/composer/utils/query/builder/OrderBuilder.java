package skyglass.composer.utils.query.builder;

import java.util.ArrayList;
import java.util.List;

import skyglass.composer.utils.query.QueryTranslationUtil;

/**
 * This class allows to build order fields from QueryRequestDTO in a declarative way.
 * Each OrderField class contains information on how to build correspondent SQL ORDER BY part
 * 
 */
public class OrderBuilder {

	private List<OrderField> orderFields = new ArrayList<OrderField>();

	private QueryRequestDTO queryRequest;

	public OrderBuilder(QueryRequestDTO queryRequest) {
		this.queryRequest = queryRequest;
	}

	public OrderBuilder bindOrder(String alias, String... orderFields) {
		return bindOrder(alias, FieldType.String, orderFields);
	}

	public OrderBuilder bindOrder(String alias, FieldType fieldType, String... orderFields) {
		if (alias.equals(queryRequest.getOrderField())) {
			setOrder(queryRequest.getOrderType(), fieldType, orderFields);
		}
		return this;
	}

	public OrderBuilder bindTranslatableOrder(String alias, String... orderFields) {
		return bindOrder(alias, FieldType.String, QueryTranslationUtil.getTranslatedFields(getCurrentLang(), orderFields));
	}

	public OrderBuilder addOrder(OrderType orderType, String... orderFields) {
		return addOrder(orderType, FieldType.String, orderFields);
	}

	public OrderBuilder addOrder(OrderType orderType, FieldType fieldType, String... orderFields) {
		OrderField order = new OrderField(new FieldResolver(orderFields), orderType, fieldType);
		this.orderFields.add(order);
		return this;
	}

	public OrderBuilder setOrder(OrderType orderType, String... orderFields) {
		return setOrder(orderType, FieldType.String, orderFields);
	}

	public OrderBuilder setOrder(OrderType orderType, FieldType fieldType, String... orderFields) {
		this.orderFields.clear();
		addOrder(orderType, fieldType, orderFields);
		return this;
	}

	public OrderBuilder setDefaultTranslatableOrder(OrderType orderType, String... orderFields) {
		return setDefaultOrder(orderType, FieldType.String, QueryTranslationUtil.getTranslatedFields(getCurrentLang(), orderFields));
	}

	public OrderBuilder setDefaultTranslatableOrders(OrderType orderType, String... orderFields) {
		return setDefaultOrders(orderType, FieldType.String, QueryTranslationUtil.getTranslatedFields(getCurrentLang(), orderFields));
	}

	public OrderBuilder setDefaultOrder(OrderType orderType, String... orderFields) {
		return setDefaultOrder(orderType, FieldType.String, orderFields);
	}

	public OrderBuilder setDefaultOrders(OrderType orderType, String... orderFields) {
		return setDefaultOrders(orderType, FieldType.String, orderFields);
	}

	public OrderBuilder setDefaultOrder(OrderType orderType, FieldType fieldType, String... orderFields) {
		if (this.orderFields.size() == 0) {
			setOrder(orderType, fieldType, orderFields);
		}
		return this;
	}

	public OrderBuilder setDefaultOrders(OrderType orderType, FieldType fieldType, String... orderFields) {
		if (this.orderFields.size() == 0) {
			for (String orderField : orderFields) {
				addOrder(orderType, fieldType, orderField);
			}
		}
		return this;
	}

	public List<OrderField> getOrderFields() {
		return orderFields;
	}

	private String getCurrentLang() {
		return queryRequest == null ? null : queryRequest.getLang();
	}

}
