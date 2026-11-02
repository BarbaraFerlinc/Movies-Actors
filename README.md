# Movie and actor service

Backend for managing movies and actors via a REST API. Movies and actors are served by **one deployable service**.

**Stack:** Java 25, Quarkus 3, Jakarta REST, CDI, Hibernate ORM with Panache (JPA), Hibernate Validator (Bean Validation), REST Jackson, H2 (file-based database), Maven, Docker.

## Run

```bash
# Docker Compose (data persisted in a volume)
docker compose up --build

# or plain Docker
docker build -t movie-actor-service . && docker run -p 8080:8080 -v movie-actor-data:/data movie-actor-service

# or locally (Java 25 + Maven)
mvn quarkus:dev
mvn test
```

## API

| Method | Path | Description |
|---|---|---|
| POST | `/api/actors/`, `/api/movies/` | create |
| GET | `/api/actors/{actorId}`, `/api/movies/{imdbId}` | read |
| PUT | `/api/actors/{actorId}`, `/api/movies/{imdbId}` | update |
| GET | `/api/actors/all`, `/api/movies/all` | list all actors/movies |
| DELETE | `/api/actors/{actorId}`, `/api/movies/{imdbId}` | delete |
| GET | `/api/actors?q=&page=0&size=20` | paginated list; `q` searches name |
| GET | `/api/movies?q=&page=0&size=20` | paginated list; `q` searches title/description |
| GET | `/api/stats/requests` | total number of REST calls and breakdown per HTTP method |

### JSON examples:

Movie JSON: `{"imdbId":"tt0133093","title":"The Matrix","year":1999,"genres":["sci-fi"],"runtimeMinutes":136,"description":"...","pictures":["https://..."],"actorIds":[1]}`

Actor JSON: `{"id":1,"firstName":"Keanu","lastName":"Reeves","birthDate":"1964-09-02","pictures":["https://..."],"movieIds":["tt0133093"]}`
