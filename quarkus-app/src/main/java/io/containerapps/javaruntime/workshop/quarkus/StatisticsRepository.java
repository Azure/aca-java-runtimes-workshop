package io.containerapps.javaruntime.workshop.quarkus;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class StatisticsRepository implements PanacheRepository<Statistics> {
}
