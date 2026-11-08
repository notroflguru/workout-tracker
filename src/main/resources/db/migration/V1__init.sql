-- 1. Таблица ПОЛЬЗОВАТЕЛЕЙ (users)
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       login VARCHAR(255) NOT NULL UNIQUE,
                       name VARCHAR(255),
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

-- 2. Таблица СПРАВОЧНИКА УПРАЖНЕНИЙ (exercise)
CREATE TABLE exercise (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          description TEXT NOT NULL
);

-- 3. Вспомогательная таблица МЫШЦ для упражнений (@ElementCollection)
CREATE TABLE exercise_muscles (
                                  exercise_id BIGINT NOT NULL,
                                  muscles VARCHAR(100) NOT NULL,
                                  CONSTRAINT fk_exercise_muscles_exercise FOREIGN KEY (exercise_id) REFERENCES exercise(id) ON DELETE CASCADE
);

-- 4. Таблица ТРЕНИРОВОК (workout) с добавленной колонкой user_id
CREATE TABLE workout (
                         id BIGSERIAL PRIMARY KEY,
                         date DATE NOT NULL,
                         notes TEXT,
                         user_id BIGINT NOT NULL,
                         CONSTRAINT fk_workout_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. Таблица СВЯЗИ ТРЕНИРОВКИ И УПРАЖНЕНИЯ (workout_exercise)
CREATE TABLE workout_exercise (
                                  id BIGSERIAL PRIMARY KEY,
                                  workout_id BIGINT NOT NULL,
                                  exercise_id BIGINT NOT NULL,
                                  order_number INT NOT NULL,
                                  CONSTRAINT fk_workout_exercise_workout FOREIGN KEY (workout_id) REFERENCES workout(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_workout_exercise_exercise FOREIGN KEY (exercise_id) REFERENCES exercise(id) ON DELETE RESTRICT
);

-- 6. Таблица ПОДХОДОВ (exercise_set)
CREATE TABLE exercise_set (
                              id BIGSERIAL PRIMARY KEY,
                              weight NUMERIC(19, 2), -- соответствует BigDecimal в Java
                              reps INT NOT NULL,
                              rir VARCHAR(50),
                              workout_exercise_id BIGINT NOT NULL,
                              order_number INT NOT NULL,
                              CONSTRAINT fk_exercise_set_workout_exercise FOREIGN KEY (workout_exercise_id) REFERENCES workout_exercise(id) ON DELETE CASCADE
);
