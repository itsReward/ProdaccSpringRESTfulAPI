package org.prodacc.webapi.repositories;

import org.prodacc.webapi.models.Jobcard;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface JobCardRepository: CrudRepository<Jobcard, UUID> 
