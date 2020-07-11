package skyglass.composer.dto.i18n;

import javax.validation.constraints.NotNull;

public class TranslationMapping {

	private final TranslationEntityMapping entityMapping;

	private final TranslationDtoMapping dtoMapping;

	private TranslationMapping(@NotNull TranslationEntityMapping entityMapping,
			@NotNull TranslationDtoMapping dtoMapping) {
		this.entityMapping = entityMapping;
		this.dtoMapping = dtoMapping;
	}

	public static TranslationMapping create(TranslationEntityMapping entityMapping, TranslationDtoMapping dtoMapping) {
		return new TranslationMapping(entityMapping, dtoMapping);
	}

	public TranslationEntityMapping getEntityMapping() {
		return entityMapping;
	}

	public TranslationDtoMapping getDtoMapping() {
		return dtoMapping;
	}
}
