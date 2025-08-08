package com.hfgroup.mortgage.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "loan.applications", groupId = "application-group")
    public void consumeLoanApplicationEvents(String message) {
        System.out.println("\nReceived Event from loan.applications topic: \n" + message);
    }

    @KafkaListener(topics = "application-created", groupId = "application-group")
    public void consumeApplicationCreated(String message) {
        System.out.println("\nReceived Application Created Event: " + message);
    }

    @KafkaListener(topics = "application-fetched", groupId = "application-group")
    public void consumeApplicationFetched(String message) {
        System.out.println("Received Application Fetched Event: " + message);
    }

    @KafkaListener(topics = "applications-fetched-with-filters", groupId = "application-group")
    public void consumeApplicationsFetchedWithFilters(String message) {
        System.out.println("Received Applications Fetched With Filters Event: " + message);
    }

    @KafkaListener(topics = "all-applications-fetched", groupId = "application-group")
    public void consumeAllApplicationsFetched(String message) {
        System.out.println("Received All Applications Fetched Event: " + message);
    }

    @KafkaListener(topics = "decision-created", groupId = "application-group")
    public void consumeDecisionCreated(String message) {
        System.out.println("Received Decision Created Event: " + message);
    }
}