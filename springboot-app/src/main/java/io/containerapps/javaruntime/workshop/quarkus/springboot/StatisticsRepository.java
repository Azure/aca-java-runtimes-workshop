package io.containerapps.javaruntime.workshop.quarkus.springboot;


import org.springframework.data.repository.CrudRepository;

interface StatisticsRepository extends CrudRepository<Statistics, Long> {
}
