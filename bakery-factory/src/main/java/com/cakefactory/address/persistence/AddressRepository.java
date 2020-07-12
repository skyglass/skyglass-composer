package com.cakefactory.address.persistence;

import org.springframework.data.repository.CrudRepository;

interface AddressRepository extends CrudRepository<AddressEntity, String> {
}
