package io.containerapps.javaruntime.workshop.quarkus.springboot;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface StatisticsRepository extends CrudRepository<Statistics, Long> {
}
