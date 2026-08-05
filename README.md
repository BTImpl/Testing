# Spring Boot Testing
## Goal
This repository doesn't contain any runnable Java application. Every code in the source are just sample codes to help the testing process.

The main contents are in the test section.

---

## 🔑 Key Takeaways

1. **Slice tests (`@WebMvcTest`, `@DataJpaTest`)** give immediate feedback and run in milliseconds. Use them for rapid unit validation.
2. **`@ServiceConnection` in Spring Boot 3.1+** eliminates manual JDBC property wiring for Testcontainers.
3. **E2E tests (`@SpringBootTest`)** are essential for verifying the glue between Security, Web, Data, and External HTTP clients.

<a href="https://btimpl.eu">
<img src="img.png">
</a>