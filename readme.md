## Bali Events Bot

Этот проект Spring Boot Maven, `bali-events-bot`, направлен на предоставление функциональности для скрапинга данных о событиях с веб-сайтов и их отображения
через Telegram бота, специально настроенного для пользователей на Бали.

### Структура проекта

- **data-scrapper**: Модуль, отвечающий за скрапинг данных с различных веб-сайтов для последующей обработки.
- **telegram-bot**: Модуль, содержащий реализацию Telegram бота для отображения событий на Бали.

### Используемые технологии

- **Spring Boot 3.2**
- **Java 21**
- **PostgreSQL**
- **MapStruct**: Используется для отображения между различными типами Java bean.
- **Selenium**: Используется для веб-скрапинга для извлечения данных о событиях с веб-сайтов.
- **SonarQube** [https://sonarcloud.io/project/overview?id=SimakovIgor_bali-events-bot]
- **Git Hub Actions** CI/CD [https://github.com/SimakovIgor/bali-events-bot/actions]
- **Docker Hub** Используется для хранения образов https://hub.docker.com/repositories/simakoff
- **Miro** Испольщуется для визуализации
  процессов https://miro.com/welcomeonboard/M1BqZVhRWDNLRlV0ZTVmZDRzMm45Ym1pN1pQbzk5bmRMeXlwYm9UQm1Da0E5WjhQNGtQSGhXdm1SRG9aWER0VXwzMDc0NDU3MzQ2NTY5NjM3NDEwfDI=?share_link_id=148011364776

### Модули

#### 1. Data Scrapper

Этот модуль посвящен скрапингу данных с веб-сайтов. Он включает функциональности для доступа к веб-страницам,
извлечения актуальной информации и хранения ее в базе данных.

- http://localhost:7900/ Virtual Network Computing (визуализация селениум процессов в браузере) (pass: secret )
- http://localhost:4445/ui# selenium grid (позволяет параллельно запускать много браузеров на разных машинах)

#### 2. Telegram Bot

Модуль Telegram бота облегчает взаимодействие с пользователями. Он извлекает данные о событиях из
базы данных и предоставляет их пользователям через сообщения Telegram.

### Инструкции по локальному запуску установке

1. Клонируйте репозиторий: `git clone [https://github.com/SimakovIgor/bali-events-bot.git]`
2. Перейдите в директорию проекта: `cd bali-events-bot`
3. Соберите проект: `mvn clean install`
4. Запустите `docker compose -f docker-compose.yaml -p bali-events up -d` [docker-compose.yaml](docker-compose.yaml)
5. Запустите приложение: `java -jar [module_name].jar`

### Лицензия

Этот проект лицензирован по Apache License - подробности см. в файле [LICENSE](LICENSE).

### Контакты

По всем вопросам или проблемам обращайтесь контактам в профиле github.

Не стесняйтесь настраивать этот файл `README.md` в соответствии с конкретными деталями и требованиями вашего проекта.
