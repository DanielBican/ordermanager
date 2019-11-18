package com.danielbican.ordermanager.repository;

import com.danielbican.ordermanager.mongo.documents.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, ObjectId> {}
