CREATE TABLE ds_example_teachers (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE ds_example_styles (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE ds_example_branches (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE ds_example_levels (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE ds_example_prices (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL
);

CREATE TABLE ds_example_schedules (
    id SERIAL PRIMARY KEY,
    time_from TIME NOT NULL,
    weekday INTEGER NOT NULL,
    branch_id INTEGER NOT NULL,
    teacher_id INTEGER NOT NULL,
    style_id INTEGER NOT NULL,
    level_id INTEGER,

    CONSTRAINT fk_schedule_branch
        FOREIGN KEY (branch_id)
        REFERENCES ds_example_branches(id),

    CONSTRAINT fk_schedule_teacher
        FOREIGN KEY (teacher_id)
        REFERENCES ds_example_teachers(id),

    CONSTRAINT fk_schedule_style
        FOREIGN KEY (style_id)
        REFERENCES ds_example_styles(id),

    CONSTRAINT fk_schedule_level
        FOREIGN KEY (level_id)
        REFERENCES ds_example_levels(id)
);

CREATE TABLE ds_example_workshops (
    id SERIAL PRIMARY KEY,
    time_from TIME NOT NULL,
    time_to TIME NOT NULL,
    name VARCHAR(255) NOT NULL,
    dates VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    date_start DATE NOT NULL,

    branch_id INTEGER NOT NULL,
    style_id INTEGER NOT NULL,


    CONSTRAINT fk_workshops_branch
        FOREIGN KEY (branch_id)
        REFERENCES ds_example_branches(id),

    CONSTRAINT fk_workshops_style
        FOREIGN KEY (style_id)
        REFERENCES ds_example_styles(id)
);