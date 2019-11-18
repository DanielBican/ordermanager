package com.danielbican.ordermanager.repository;

import com.danielbican.ordermanager.mongo.documents.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, ObjectId> {

  List<Order> findAllByPlacedBetween(LocalDateTime from, LocalDateTime to);
}
