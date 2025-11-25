## Architectural Trade-offs: Layering vs Partitioning

### Layered Monolith Approach

We chose a layered monolithic architecture (for now) with four clear layers (Presentation, Application, Domain, Infrastructure) rather than partitioning into multiple services for several reasons.

A single deployable service is easier to develop, test, and deploy for a small café POS system such as this. There was no need to overcomplicate things for such a small project with a defined scope. Performance was also taken into consideration, as in-process method calls are faster than network communication. With a monolith architecture, development speed is quicker as there are no complexity issues due to distributed systems such as API contracts, network communication, and consistency issues (all learned about simultaneously in another module, Applied System Design with Andrew Ju). On top of all that, as we were such a small team of 2, working in a single codebase was not awkward and actually helped with our understanding of the system.

### Future Partitioning Candidates

In our opinion, the natural seams for future microservices include:

- **Payment Service**: An external payment gateway integration could be isolated with clear transactional boundaries
- **Notification Service**: Kitchen displays, customer SMS, and delivery alerts could run independently via event streaming
- **Analytics Service**: Order metrics and reporting don't need real-time synchronous access to the core POS system
- **Inventory Service**: Update menu accordingly and could integrate with suppliers

These seams seem obvious to us as they have clear boundaries and different scaling needs. They could all fail independently without bringing down our whole POS system.

### Connectors for Distributed Architecture

Currently we do direct method calls (e.g., `controller.checkout(orderId);`) which have benefits such as being in-process, fast, and simple. However, if splitting services into a distributed system, we would use:

- **REST APIs** for synchronous operations such as checkout and order queries. These API endpoints will be used when an immediate response is needed.
- **Message Queues** (RabbitMQ/Kafka learned in Applied System Design module) for asynchronous events such as order created and payment processed. They are useful for fire-and-forget actions, multiple consumers, and for when eventual consistency is okay. Could be used for notifications and analytics.
- **Event Bus** pattern would become a distributed message broker
- **API Gateway** for frontend to access multiple services through a single endpoint

Our current EventBus demonstrates the publish-subscribe pattern that would scale to a message broker in a distributed system.