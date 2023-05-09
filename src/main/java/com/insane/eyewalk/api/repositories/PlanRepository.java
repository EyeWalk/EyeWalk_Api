package com.insane.eyewalk.api.repositories;

import com.insane.eyewalk.api.model.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {

}