# TODO

This file tracks technical debt and planned improvements for this project.  
For high-level tasks, see the "TODO" section in the [README.md](./README.md).

## Application Improvements

- [ ] Monitoring with Prometheus & Grafana  
_Add dependencies: `spring-boot-starter-actuator` and `micrometer-registry-prometheus`._  
_Expose metrics endpoint: set `management.endpoints.web.exposure.include=health,info,prometheus`._  
_Run Prometheus and Grafana (docker-compose) and configure Prometheus to scrape the app’s `/actuator/prometheus` endpoint._  
_Import a JVM/Spring dashboard in Grafana and set alerts as needed._  
- [ ] Write Tests  

## Containerization

- [ ] Create Dockerfiles for both the command and query services  
  _Provide Dockerfiles in each service’s directory for consistent container builds._

## Continuous Integration

- [ ] Implement a CI pipeline to build Docker images and publish them to a container registry  
  _Set up GitHub Actions or another CI system to automate Docker builds and registry push on each merge to main branch._

---

_Expand this file as new tasks arise. Completed items can be moved to a changelog or marked as done._