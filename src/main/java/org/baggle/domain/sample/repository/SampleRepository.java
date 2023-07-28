package org.baggle.domain.sample.repository;


import org.baggle.domain.sample.entity.Sample;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SampleRepository extends JpaRepository<Sample, Long> {
    List<Sample> findByData(String data);
}
