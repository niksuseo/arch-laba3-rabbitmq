\# Лабораторная работа №4 — Observability



\## Цель работы



Реализовать мониторинг микросервисного приложения с использованием Spring Boot Actuator, Micrometer, Prometheus, Grafana и Alertmanager.



\## Реализованные компоненты



\- \*\*Spring Boot Actuator\*\* — предоставляет метрики Java-приложения.

\- \*\*Micrometer\*\* — регистрирует стандартные и пользовательские метрики.

\- \*\*Prometheus\*\* — собирает метрики приложения и RabbitMQ.

\- \*\*Grafana\*\* — визуализирует собранные данные.

\- \*\*Alertmanager\*\* — обрабатывает оповещения о проблемах.

\- \*\*Mailpit\*\* — принимает тестовые уведомления по электронной почте.



\## Пользовательские метрики



В `user-service` реализованы:



1\. `user\_requests\_total` — Counter, подсчитывающий обращения к GET /users.

2\. `user\_api\_operations\_total` — Counter с меткой `operation`, разделяющей операции получения, создания и удаления пользователей.

3\. `user\_get\_all\_duration\_seconds` — Timer, измеряющий время получения списка пользователей.



\## Дашборды Grafana



Созданы три дашборда:



\- \*\*Lab4 - JVM Monitoring\*\* — оперативная память JVM, загрузка CPU, HTTP-запросы и пользовательские операции.

\- \*\*Lab4 - RabbitMQ Monitoring\*\* — сообщения в очередях, активные соединения и количество потребителей.

\- \*\*Lab4 - Service Health Monitoring\*\* — доступность сервисов, время сбора метрик и время работы Java-приложения.



JSON-файлы дашбордов хранятся в `grafana/dashboards` и автоматически импортируются через Grafana provisioning.



\## Правила оповещений



Настроены три правила:



\- `UserServiceDown` — недоступность user-service.

\- `RabbitMQDown` — недоступность RabbitMQ.

\- `RabbitMQQueueBacklog` — накопление более 10 сообщений в очередях.



Проверена отправка уведомления `UserServiceDown` из Prometheus через Alertmanager в Mailpit.



\## Запуск системы



\### Предварительные требования



\- Docker Desktop с поддержкой Docker Compose.

\- Java 17 и Maven Wrapper.

\- Запущенные PostgreSQL, KeyDB и RabbitMQ из предыдущих лабораторных.



RabbitMQ должен быть подключён к Docker-сети `lab4-monitoring` и иметь включённый плагин `rabbitmq\_prometheus`.



\### Команды запуска



Из корня репозитория:



```powershell

docker network create lab4-monitoring

docker network connect lab4-monitoring rabbitmq-standalone-lab3

```



Если сеть уже существует или RabbitMQ уже подключён, повторное создание или подключение не требуется.



Запуск мониторинга:



```powershell

docker compose -f .\\monitoring\\compose.yaml up -d

```



Сборка Java-сервиса:



```powershell

.\\mvnw.cmd -pl user-service -am clean package -DskipTests

```



Запуск Java-сервиса:



```powershell

java -jar .\\user-service\\target\\user-service-0.0.1-SNAPSHOT.jar

```



\### Веб-интерфейсы



| Компонент | Адрес |

|---|---|

| Spring Boot Actuator | http://localhost:8080/actuator/prometheus |

| Prometheus | http://localhost:9090 |

| Grafana | http://localhost:3000 |

| Alertmanager | http://localhost:9093 |

| Mailpit | http://localhost:8025 |

| RabbitMQ Management | http://localhost:15671 |



\## Проверка работы



1\. Убедиться, что в Prometheus Targets сервисы `user-service` и `rabbitmq` имеют статус `UP`.

2\. Открыть три дашборда Grafana и проверить наличие данных.

3\. Вызвать `GET /users` и проверить увеличение пользовательских счётчиков.

4\. Проверить правила в Prometheus Alerts.

5\. При контролируемой остановке `user-service` убедиться, что `UserServiceDown` переходит в `Firing`, а Mailpit получает письмо.

6\. Восстановить работу Java-сервиса.



\## Результат



Разработана система наблюдаемости микросервисного приложения. Реализованы сбор стандартных и пользовательских метрик, визуализация состояния приложения и RabbitMQ, автоматическое обнаружение проблем и отправка тестовых уведомлений.

