INSERT INTO public.user(username, password, role, enabled) values ('admin', '$2a$12$gze29bZcYYpc14HzVb58jeMxWK/A9tV5EA54inlf5P/DxeBrB.mVO', 'ADMIN', true);

INSERT INTO public.director(fio) values ('Тестов1 Тест1 Тест1');
INSERT INTO public.director(fio) values ('Тестов2 Тест2 Тест2');

INSERT INTO public.movie(title, year, director, duration, rating) values ('Test', 2000, 2, '2:30', 7.2);