package io.containerapps.javaruntime.workshop.micronaut;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Repository
@Transactional
interface StatisticsRepository extends CrudRepository<Statistics, Long> {
}
