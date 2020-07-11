package skyglass.composer.dto.i18n;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.entity.AEntity;

public class TranslationRegistry implements AutoCloseable {
	private static final ThreadLocal<TranslationRegistry> threadLocal = new ThreadLocal<>();

	private final Map<AEntity, Pair<List<Serializable>, Map<String, TranslationMapping[]>>> registry;

	private TranslationRegistry() {
		this.registry = new HashMap<>();
	}

	@NotNull
	public static TranslationRegistry get() {
		TranslationRegistry instance;

		instance = threadLocal.get();
		if (instance == null) {
			instance = new TranslationRegistry();

			threadLocal.set(instance);
		}

		return instance;
	}

	@Override
	public void close() throws Exception {
		if (registry != null) {
			registry.clear();
		}

		threadLocal.remove();
	}

	public void register(AEntity entity, Serializable dto, TranslationMapping translationMapping)
			throws IllegalArgumentException {
		register(entity, dto, translationMapping, new TranslationMapping[0]);
	}

	public void register(AEntity entity, Serializable dto, TranslationMapping translationMapping,
			TranslationMapping... translationMappings) throws IllegalArgumentException, UnsupportedOperationException {
		if (entity == null) {
			throw new IllegalArgumentException("entity cannot be null");
		}

		if (dto == null) {
			throw new IllegalArgumentException("dto cannot be null");
		}

		if (translationMapping == null) {
			throw new IllegalArgumentException("translationMapping cannot be null");
		}

		if (translationMappings != null && translationMappings.length > 0) {
			translationMappings = ArrayUtils.insert(0, translationMappings, translationMapping);
		} else {
			translationMappings = new TranslationMapping[] { translationMapping };
		}

		String dtoClassName = dto.getClass().getName();
		Pair<List<Serializable>, Map<String, TranslationMapping[]>> value = getPairByEntity(entity);
		if (value != null) {
			Map<String, TranslationMapping[]> mappingsMap = value.getRight();
			TranslationMapping[] currentMapping = mappingsMap.get(dtoClassName);
			if (currentMapping != null && !Arrays.equals(currentMapping, translationMappings)) {
				throw new UnsupportedOperationException(
						"There was already another translation mapping for the DTO type " + dtoClassName);
			}

			mappingsMap.put(dtoClassName, translationMappings);

			List<Serializable> dtos = value.getLeft();
			dtos.add(dto);

			value = Pair.of(dtos, mappingsMap);
		} else {
			Map<String, TranslationMapping[]> mappingsMap = new HashMap<>();
			mappingsMap.put(dtoClassName, translationMappings);

			List<Serializable> dtos = new ArrayList<>();
			dtos.add(dto);

			value = Pair.of(dtos, mappingsMap);
		}

		registry.put(entity, value);
	}

	@NotNull
	public Collection<AEntity> getEntities() {
		return Collections.unmodifiableCollection(registry.keySet());
	}

	public List<Serializable> getDtosByEntity(AEntity entity) throws IllegalArgumentException {
		Pair<List<Serializable>, Map<String, TranslationMapping[]>> pair = getPairByEntity(entity);
		if (pair != null) {
			return pair.getLeft();
		}

		return null;
	}

	@NotNull
	public TranslationMapping[] getMappingsByEntity(AEntity entity, Class<?> dtoClass) throws IllegalArgumentException {
		return getMappingsByEntity(entity, dtoClass.getName());
	}

	@NotNull
	public TranslationMapping[] getMappingsByEntity(AEntity entity, String dtoClassName)
			throws IllegalArgumentException {
		Pair<List<Serializable>, Map<String, TranslationMapping[]>> pair = getPairByEntity(entity);
		if (pair != null) {
			TranslationMapping[] mappings = pair.getRight().get(dtoClassName);
			if (mappings != null) {
				return mappings;
			}
		}

		return new TranslationMapping[0];
	}

	private Pair<List<Serializable>, Map<String, TranslationMapping[]>> getPairByEntity(AEntity entity)
			throws IllegalArgumentException {
		if (entity == null) {
			throw new IllegalArgumentException("entity cannot be null");
		}

		return registry.get(entity);
	}
}
