package skyglass.composer.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.domain.IIdentifiable;
import skyglass.composer.entity.AEntity;

public class AEntityDTOFactory {

	public static <T extends IIdentifiable> List<String> provideUuidsFromReferences(Collection<T> entities) {
		return provideUuidsFromReferences(entities, null);
	}

	public static <T extends IIdentifiable> List<String> provideUuidsFromReferences(Collection<T> entities,
			Predicate<T> filter) {
		if (entities == null) {
			return null;
		}

		Stream<T> stream = entities.stream();
		if (filter != null) {
			stream = stream.filter(filter);
		}

		return stream.filter(e -> e != null && StringUtils.isNotBlank(e.getUuid())).map(e -> e.getUuid()).distinct().collect(Collectors.toList());
	}

	public static String provideUuidFromReference(IIdentifiable entity) {
		if (entity != null) {
			return entity.getUuid();
		}

		return null;
	}

	public static <DTO extends AEntityDTO, E extends AEntity> DTO createVeryBasicDto(E entity,
			Supplier<DTO> constructor) {
		if (entity == null || constructor == null) {
			return null;
		}

		DTO dto = constructor.get();
		dto.setUuid(entity.getUuid());

		return dto;
	}

	public static <E extends AEntity> E createVeryBasicEntity(String uuid, Supplier<E> constructor) {
		if (constructor == null || StringUtils.isBlank(uuid)) {
			return null;
		}

		E entity = constructor.get();
		entity.setUuid(uuid);

		return entity;
	}

	@NotNull
	protected static <E extends AEntity> List<E> createVeryBasicEntities(List<String> uuids, Supplier<E> constructor) {
		if (uuids == null || uuids.isEmpty() || constructor == null) {
			return new ArrayList<>();
		}

		return uuids.stream().map(uuid -> createVeryBasicEntity(uuid, constructor)).collect(Collectors.toList());
	}
}
