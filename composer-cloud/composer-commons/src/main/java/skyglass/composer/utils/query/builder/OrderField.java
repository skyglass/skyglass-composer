package skyglass.composer.utils.query.builder;

/**
 * This class contains information on how to build correspondent SQL ORDER BY part
 * (See QueryOrderUtil class, which converts the list of OrderField classe to correspondent SQL ORDER BY part)
 * 
 */
public class OrderField {

	private FieldResolver fieldResolver;

	private OrderType orderType;

	private FieldType fieldType;

	public OrderField(FieldResolver fieldResolver, OrderType orderType) {
		this(fieldResolver, orderType, FieldType.String);
	}

	public OrderField(FieldResolver fieldResolver, OrderType orderType, FieldType fieldType) {
		this.fieldResolver = fieldResolver;
		this.orderType = orderType;
		this.fieldType = fieldType;
	}

	public FieldResolver getOrderField() {
		return fieldResolver;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public boolean isDescending() {
		return orderType == OrderType.Desc;
	}

	public boolean isMultiple() {
		return fieldResolver.isMultiple();
	}

	public boolean isSingle() {
		return fieldResolver.isSingle();
	}

	public boolean isString() {
		return fieldType == FieldType.String;
	}

}
