package io.containerapps.javaruntime.workshop.quarkus;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class StatisticsRepository implements PanacheRepository<Statistics> {
}
