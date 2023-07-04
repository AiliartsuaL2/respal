package com.hckst.respal.members.domain.repository;

import com.hckst.respal.members.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job,Integer> {
}
