package skyglass.composer.dto.i18n;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import skyglass.composer.domain.IIdentifiable;
import skyglass.composer.dto.AEntityDTO;
import skyglass.composer.dto.AEntityDTOFactory;
import skyglass.composer.entity.AEntity;
import skyglass.composer.entity.i18n.SupportedLanguage;
import skyglass.composer.entity.i18n.TranslatedField;

public class TranslatedFieldDTOFactory extends AEntityDTOFactory {

	public static TranslatedField createTranslatedField(TranslatedFieldDTO translatedFieldDTO) {
		if (translatedFieldDTO == null) {
			return null;
		}

		TranslatedField entity = new TranslatedField();
		entity.setUuid(translatedFieldDTO.getUuid());
		entity.setEn(translatedFieldDTO.getEn());
		entity.setReferenceUuid(translatedFieldDTO.getReferenceUuid());

		return entity;
	}

	public static TranslatedFieldDTO createTranslatedFieldDTO(TranslatedField translatedField) {
		if (translatedField == null) {
			return null;
		}

		TranslatedFieldDTO dto = new TranslatedFieldDTO();
		dto.setUuid(translatedField.getUuid());
		dto.setEn(translatedField.getEn());
		dto.setReferenceUuid(translatedField.getReferenceUuid());

		return dto;
	}

	public static TranslatedField copyTranslatedField(TranslatedField translatedField) {
		if (translatedField == null) {
			return null;
		}
		TranslatedField field = new TranslatedField();
		field.setEn(translatedField.getEn());
		return field;
	}

	@NotNull
	public static Map<String, Map<String, TranslatedFieldDTO>> createTranslatedFieldDTOs(
			Map<String, Map<String, TranslatedField>> translatedFields) {
		return translatedFields.values().stream().flatMap(entityMap -> entityMap.values().stream())
				.map(entity -> createTranslatedFieldDTO(entity)).collect(Collectors
						.groupingBy(dto -> dto.getReferenceUuid(), Collectors.toMap(dto -> dto.getUuid(), dto -> dto)));
	}

	public static String getTranslation(TranslatedFieldDTO dto, SupportedLanguage language) {
		return getTranslation(dto, language, false);
	}

	public static String getTranslation(TranslatedFieldDTO dto, SupportedLanguage language,
			boolean fallbackToBestTranslation) {
		if (dto == null) {
			return null;
		}

		if (language == null) {
			language = SupportedLanguage.DEFAULT;
		}

		String value = null;

		switch (language) {
			case EN:
				value = dto.getEn();
				break;
		}

		if (StringUtils.isBlank(value) && fallbackToBestTranslation) {
			return getBestTranslation(dto);
		}

		return value;
	}

	public static String getTranslation(TranslatedField entity, SupportedLanguage language) {
		return getTranslation(entity, language, false);
	}

	public static String getTranslation(TranslatedField entity, SupportedLanguage language,
			boolean fallbackToBestTranslation) {
		if (entity == null) {
			return null;
		}

		if (language == null) {
			language = SupportedLanguage.DEFAULT;
		}

		String value = null;

		switch (language) {
			case EN:
				value = entity.getEn();
				break;
		}

		if (StringUtils.isBlank(value) && fallbackToBestTranslation) {
			return getBestTranslation(entity);
		}

		return value;
	}

	public static String getBestTranslation(TranslatedFieldDTO dto) {
		if (dto == null) {
			return null;
		}

		for (SupportedLanguage lang : SupportedLanguage.values()) {
			try {
				String result = (String) PropertyUtils.getSimpleProperty(dto, lang.getLanguageCode());
				if (StringUtils.isNotBlank(result)) {
					return result;
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new IllegalArgumentException(String.format("Language code: '%' is not supported: please update TranslatedFieldDTO", lang.getLanguageCode()), e);
			}
		}

		return null;
	}

	public static String getBestTranslation(TranslatedField entity) {
		if (entity == null) {
			return null;
		}

		for (SupportedLanguage lang : SupportedLanguage.values()) {
			try {
				String result = (String) PropertyUtils.getSimpleProperty(entity, lang.getLanguageCode());
				if (StringUtils.isNotBlank(result)) {
					return result;
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new IllegalArgumentException(String.format("Language code: '%' is not supported: please update TranslatedField Entity", lang.getLanguageCode()), e);
			}
		}

		return null;
	}

	public static String getDefaultTranslation(TranslatedFieldDTO dto) {
		return getTranslation(dto, SupportedLanguage.DEFAULT);
	}

	public static String getDefaultTranslation(TranslatedField entity) {
		return getTranslation(entity, SupportedLanguage.DEFAULT);
	}

	public static boolean isEmpty(TranslatedField entity) {
		return entity == null || StringUtils.isAllBlank(entity.getEn());
	}

	public static boolean isEmpty(TranslatedFieldDTO dto) {
		return dto == null || StringUtils.isAllBlank(dto.getEn());
	}

	public static TranslatedFieldDTO setTranslation(TranslatedFieldDTO dto, String text, SupportedLanguage language) {
		if (text == null) {
			return dto;
		}

		if (dto == null) {
			dto = new TranslatedFieldDTO();
		}

		if (language == null) {
			language = SupportedLanguage.DEFAULT;
		}

		switch (language) {
			case EN:
				dto.setEn(text);
				break;
		}

		return dto;
	}

	public static TranslatedField setTranslation(TranslatedField entity, String text, SupportedLanguage language) {
		if (text == null) {
			return entity;
		}

		if (entity == null) {
			entity = new TranslatedField();
		}

		if (language == null) {
			language = SupportedLanguage.DEFAULT;
		}

		switch (language) {
			case EN:
				entity.setEn(text);
				break;
		}

		return entity;
	}

	public static TranslatedFieldDTO setDefaultTranslation(TranslatedFieldDTO dto, String text) {
		return setTranslation(dto, text, SupportedLanguage.DEFAULT);
	}

	public static TranslatedField setDefaultTranslation(TranslatedField entity, String text) {
		return setTranslation(entity, text, SupportedLanguage.DEFAULT);
	}

	public static void translate(Serializable source, AEntity target, TranslationMapping mapping) {
		translate(source, mapping.getDtoMapping().getFieldName(), mapping.getDtoMapping().getFallbackFieldName(),
				target, mapping.getEntityMapping().getFieldName(), mapping.getEntityMapping().getFallbackFieldName(),
				SupportedLanguage.DEFAULT);
	}

	public static void translate(Serializable source, String sourceFieldName, String fallbackSourceFieldName,
			AEntity target, String targetFieldName, String targetFallbackFieldName, SupportedLanguage language) {
		if (target == null || StringUtils.isBlank(targetFieldName)) {
			return;
		}

		if (StringUtils.isNotBlank(sourceFieldName)) {
			TranslatedFieldDTO tfDto;

			try {
				tfDto = (TranslatedFieldDTO) PropertyUtils.getSimpleProperty(source, sourceFieldName);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
				throw new UnsupportedOperationException("Could not get " + sourceFieldName + " from " + source, ex);
			}

			try {
				PropertyUtils.setSimpleProperty(target, targetFieldName,
						TranslatedFieldDTOFactory.createTranslatedField(tfDto));
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
				throw new UnsupportedOperationException("Could not set " + targetFieldName + " to " + target, ex);
			}
		}

		TranslatedField tf;

		try {
			tf = (TranslatedField) PropertyUtils.getSimpleProperty(target, targetFieldName);
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
			throw new UnsupportedOperationException("Could not get " + targetFieldName + " from " + target, ex);
		}

		if (StringUtils.isNotBlank(fallbackSourceFieldName)) {
			String sourceValue;

			try {
				sourceValue = (String) PropertyUtils.getSimpleProperty(source, fallbackSourceFieldName);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
				throw new UnsupportedOperationException("Could not get " + fallbackSourceFieldName + " from " + source,
						ex);
			}

			if (TranslatedFieldDTOFactory.isEmpty(tf) && !StringUtils.isBlank(sourceValue)) {
				translate(sourceValue, target, targetFieldName, targetFallbackFieldName, language);
			}
		}
	}

	public static void translate(String source, AEntity target, TranslationEntityMapping mapping,
			SupportedLanguage language) {
		translate(source, target, mapping.getFieldName(), mapping.getFallbackFieldName(), language);
	}

	public static void translate(String source, AEntity target, String targetFieldName, String targetFallbackFieldName,
			SupportedLanguage language) {
		if (target == null || StringUtils.isBlank(targetFieldName)) {
			return;
		}

		TranslatedField tf;

		try {
			tf = (TranslatedField) PropertyUtils.getSimpleProperty(target, targetFieldName);
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
			throw new UnsupportedOperationException("Could not get " + targetFieldName + " from " + target, ex);
		}

		tf = setTranslation(tf, source, language);

		try {
			PropertyUtils.setSimpleProperty(target, targetFieldName, tf);
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
			throw new UnsupportedOperationException("Could not set " + targetFieldName + " to " + target, ex);
		}
		if (!StringUtils.isBlank(targetFallbackFieldName)) {
			try {
				PropertyUtils.setSimpleProperty(target, targetFallbackFieldName, source);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
				throw new UnsupportedOperationException("Could not set " + targetFallbackFieldName + " to " + target,
						ex);
			}
		}
	}

	public static void translate(String source, AEntityDTO target, TranslationDtoMapping mapping,
			SupportedLanguage language) {
		String targetFieldName = mapping.getFieldName();
		if (target == null || StringUtils.isBlank(targetFieldName)) {
			return;
		}
		TranslatedFieldDTO tf;
		try {
			tf = (TranslatedFieldDTO) PropertyUtils.getSimpleProperty(target, targetFieldName);
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
			throw new UnsupportedOperationException("Could not get " + targetFieldName + " from " + target, ex);
		}
		tf = setTranslation(tf, source, language);
		try {
			PropertyUtils.setSimpleProperty(target, targetFieldName, tf);
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
			throw new UnsupportedOperationException("Could not set " + targetFieldName + " to " + target, ex);
		}
		String targetFallbackFieldName = mapping.getFallbackFieldName();
		if (!StringUtils.isBlank(targetFallbackFieldName)) {
			try {
				PropertyUtils.setSimpleProperty(target, targetFallbackFieldName, source);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
				throw new UnsupportedOperationException("Could not set " + targetFallbackFieldName + " to " + target,
						ex);
			}
		}
	}

	public static void translate(IIdentifiable source, String sourceFieldName, String sourceFallbackFieldName,
			Serializable target, String targetFieldName, String targetFallbackFieldName,
			Map<String, ? extends Map<String, TranslatedFieldDTO>> translatedFields)
			throws IllegalArgumentException, UnsupportedOperationException {
		if (source == null || target == null) {
			return;
		}

		if (StringUtils.isBlank(sourceFieldName)) {
			throw new IllegalArgumentException("Source field name cannot be null nor empty");
		}

		if (StringUtils.isBlank(targetFieldName)) {
			throw new IllegalArgumentException("Target field name cannot be null nor empty");
		}

		String tfUuid = null;

		try {
			Field field = getDeclaredField(source.getClass(), sourceFieldName + "_uuid");
			field.setAccessible(true);
			tfUuid = (String) field.get(source);
			if (tfUuid == null) {
				//for newly created translated fields, the _uuid property might still be null, so we fallback to scenario, which triggers database call. It should happen only for creation of translated fields, so it won't affect pefrormance
				TranslatedField tf = (TranslatedField) PropertyUtils.getSimpleProperty(source, sourceFieldName);
				if (tf != null) {
					tfUuid = tf.getUuid();
				}
			}
		} catch (UnsupportedOperationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
			throw new UnsupportedOperationException("Could not get " + sourceFieldName + " from " + source, ex);
		}

		if (tfUuid != null) {
			Map<String, TranslatedFieldDTO> translatedFieldsMap = translatedFields.get(source.getUuid());
			if (translatedFieldsMap != null) {
				TranslatedFieldDTO value = translatedFieldsMap.get(tfUuid);
				if (TranslatedFieldDTOFactory.isEmpty(value) && !StringUtils.isBlank(sourceFallbackFieldName)) {
					try {
						String fallbackValue = (String) PropertyUtils.getSimpleProperty(source,
								sourceFallbackFieldName);

						value = setDefaultTranslation(value, fallbackValue);
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
						// source fallback field name does not exist for entity
						// anymore, so we cannot fallback to it anymore, but
						// that should mean that the value was already migrated
						// on the database. No need to throw an exception here
					}
				}

				if (!TranslatedFieldDTOFactory.isEmpty(value)) {
					try {
						PropertyUtils.setSimpleProperty(target, targetFieldName, value);
					} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
						throw new UnsupportedOperationException("Could not set " + targetFieldName + " to " + target,
								ex);
					}
				}
			}
		}

		if (!StringUtils.isBlank(targetFallbackFieldName)) {
			TranslatedFieldDTO value;
			try {
				value = (TranslatedFieldDTO) PropertyUtils.getSimpleProperty(target, targetFieldName);
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
				throw new UnsupportedOperationException("Could not get " + targetFieldName + " from " + target, ex);
			}

			if (!TranslatedFieldDTOFactory.isEmpty(value)) {
				try {
					PropertyUtils.setSimpleProperty(target, targetFallbackFieldName,
							TranslatedFieldDTOFactory.getBestTranslation(value));
				} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
					throw new UnsupportedOperationException(
							"Could not set " + targetFallbackFieldName + " to " + target, ex);
				}
			}
		}
	}

	public static Field getDeclaredField(Class<?> clazz, String fieldName) {
		Class<?> tmpClass = clazz;
		do {
			try {
				Field f = tmpClass.getDeclaredField(fieldName);
				f.setAccessible(true);
				return f;
			} catch (NoSuchFieldException e) {
				tmpClass = tmpClass.getSuperclass();
			}
		} while (tmpClass != null && tmpClass != Object.class);

		throw new UnsupportedOperationException("Could not get " + fieldName + " from " + clazz);
	}

	public static boolean hasTranslation(TranslatedField entity, String value) {
		if (entity == null) {
			return false;
		}
		for (SupportedLanguage lang : SupportedLanguage.getLanguages(null)) {
			if (value.equalsIgnoreCase(getTranslation(entity, lang))) {
				return true;
			}
		}
		return false;
	}
}
