-- ========================================================
-- 2. НАПОЛНЕНИЕ СПРАВОЧНИКА УПРАЖНЕНИЙ (exercise)
-- ========================================================
INSERT INTO exercise (id, name, description) VALUES
                                                 (1, 'Bench press', 'Классический жим штанги лежа на горизонтальной скамье'),
                                                 (2, 'Squat', 'Приседания со штангой на плечах'),
                                                 (3, 'Deadlift', 'Классическая становая тяга'),
                                                 (4, 'Seated leg extension', 'Разгибание ног в тренажере сидя'),
                                                 (5, 'Seated leg curl', 'Сгибание ног в тренажере сидя'),
                                                 (6, 'Lateral raises', 'Махи гантелями в стороны'),
                                                 (7, 'Chest fly', 'Сведение рук в тренажере Пек-Дек'),
                                                 (8, 'Seated cable row', 'Горизонтальная тяга к поясу в блоке'),
                                                 (9, 'Pull ups', 'Классические подтягивания на турнике'),
                                                 (10, 'Lat pull downs', 'Вертикальная тяга блока к груди сидя'),
                                                 (11, 'Hammer curls', 'Сгибания рук с гантелями'),
                                                 (12, 'Triceps extensions', 'Разгибания рук на трицепс в блоке');

-- Корректируем счетчик ID в PostgreSQL, чтобы следующие вставки (через приложение) начинались с 13
SELECT setval('exercise_id_seq', 12);

-- ========================================================
-- 3. ПРИВЯЗКА МЫШЦ К УПРАЖНЕНИЯМ (exercise_muscles)
-- ========================================================
INSERT INTO exercise_muscles (exercise_id, muscles) VALUES
-- Bench press
(1, 'CHEST'), (1, 'FRONT_DELTS'), (1, 'TRICEPS'),
-- Squat
(2, 'QUADRICEPS'), (2, 'GLUTES'), (2, 'HAMSTRINGS'),
-- Deadlift
(3, 'GLUTES'), (3, 'SPINAL_ERECTORS'), (3, 'HAMSTRINGS'),
-- Seated leg extension
(4, 'QUADRICEPS'),
-- Seated leg curl
(5, 'HAMSTRINGS'),
-- Lateral raises
(6, 'SIDE_DELTS'), (6, 'TRAPS'),
-- Chest fly
(7, 'CHEST'), (7, 'FRONT_DELTS'),
-- Seated cable row
(8, 'LATS'), (8, 'RHOMBOIDS'), (8, 'TRAPS'), (8, 'BICEPS'),
-- Pull ups
(9, 'LATS'), (9, 'RHOMBOIDS'), (9, 'BICEPS'),
-- Lat pull downs
(10, 'LATS'), (10, 'RHOMBOIDS'), (10, 'BICEPS'),
-- Hammer curls
(11, 'BRACHIORADIALIS'), (11, 'BRACHIALIS'), (11, 'BICEPS'),
-- Triceps extensions
(12, 'TRICEPS');