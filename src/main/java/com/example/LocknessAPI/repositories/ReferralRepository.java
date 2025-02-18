package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.Referral;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReferralRepository extends MongoRepository<Referral, String> {
}
