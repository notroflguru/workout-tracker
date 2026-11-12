-- 1. Ограничиваем длину строковых колонок
ALTER TABLE users
ALTER COLUMN login TYPE VARCHAR(20),
    ALTER COLUMN name TYPE VARCHAR(100),
    ALTER COLUMN email TYPE VARCHAR(255),
    ALTER COLUMN password_hash TYPE VARCHAR(255);

-- 2. Добавляем проверки (CHECK constraints) на формат и минимальную длину
ALTER TABLE users
    ADD CONSTRAINT chk_users_login_format
        CHECK (login ~ '^[a-zA-Z0-9_-]{3,20}$'),
    ADD CONSTRAINT chk_users_email_not_blank
    CHECK (LENGTH(TRIM(email)) > 0 AND email ~ '^[^@\s]+@[^@\s]+\.[^@\s]+$'),
    ADD CONSTRAINT chk_users_password_hash_not_blank
    CHECK (LENGTH(TRIM(password_hash)) > 0);

-- 3. Ограничиваем длину колонок в основной таблице
ALTER TABLE exercise
ALTER COLUMN name TYPE VARCHAR(63),
    ALTER COLUMN description TYPE VARCHAR(255);

-- 4. Добавляем проверки минимальной длины (исключая пустые строки из пробелов)
ALTER TABLE exercise
    ADD CONSTRAINT chk_exercise_name_length
        CHECK (LENGTH(TRIM(name)) >= 5),
    ADD CONSTRAINT chk_exercise_description_length
        CHECK (LENGTH(TRIM(description)) >= 5);

-- 5. Защита от дублирования одинаковых мышц в одном упражнении и ограничение длины enum
ALTER TABLE exercise_muscles
ALTER COLUMN muscles TYPE VARCHAR(50),
    ADD CONSTRAINT uq_exercise_muscles UNIQUE (exercise_id, muscles);

-- 6. Триггер для ограничения максимум 5 мышц на одно упражнение (аналог @Size(max = 5))
CREATE OR REPLACE FUNCTION check_max_muscles_per_exercise()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT COUNT(*) FROM exercise_muscles WHERE exercise_id = NEW.exercise_id) >= 5 THEN
        RAISE EXCEPTION 'Количество мышц для одного упражнения не должно превышать 5';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_limit_exercise_muscles
    BEFORE INSERT ON exercise_muscles
    FOR EACH ROW
    EXECUTE FUNCTION check_max_muscles_per_exercise();

