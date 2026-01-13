Create DATABASE reviews;
Create DATABASE users;

\connect reviews;

DROP TABLE IF EXISTS Review;
DROP TABLE IF EXISTS ReviewUser;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'rating_enum') THEN
CREATE TYPE rating_enum AS ENUM ('ZERO_STARS', 'ONE_STAR', 'TWO_STARS', 'THREE_STARS', 'FOUR_STARS', 'FIVE_STARS');
END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'status_enum') THEN
CREATE TYPE status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'DELETED', 'SUSPENDED');
END IF;
END $$;

CREATE TABLE Review (
                        review_id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        book_id BIGINT NOT NULL,
                        rating rating_enum NOT NULL,
                        title VARCHAR(100) NOT NULL,
                        description VARCHAR(300) NOT NULL
);

CREATE TABLE ReviewUser (
                            user_id BIGSERIAL PRIMARY KEY,
                            username VARCHAR(50) NOT NULL UNIQUE,
                            password VARCHAR(100) NOT NULL,
                            email VARCHAR(50) NOT NULL UNIQUE,
                            first_name VARCHAR(50),
                            last_name VARCHAR(50),
                            user_status status_enum NOT NULL,
                            created_at TIMESTAMP WITHOUT TIME ZONE,
                            updated_at TIMESTAMP WITHOUT TIME ZONE,
                            last_login TIMESTAMP WITHOUT TIME ZONE
);

INSERT INTO Review (user_id, book_id, rating, title, description) VALUES
                                                                      (1, 1, 'THREE_STARS', 'title example 1', 'description example 1'),
                                                                      (2, 1, 'FIVE_STARS', 'title example 2', 'description example 2'),
                                                                      (3, 2, 'ZERO_STARS', 'title example 3', 'description example 3'),
                                                                      (4, 3, 'FOUR_STARS', 'title example 4', 'description example 4'),
                                                                      (2, 2, 'ONE_STAR', 'title example 5', 'description example 5');

INSERT INTO ReviewUser (username, password, email, user_status, created_at) VALUES
                                                                                ('user1', 'test_password_1', 'user1@gmail.com', 'ACTIVE', NOW()),
                                                                                ('user2', 'test_password_2', 'user2@gmail.com', 'ACTIVE', NOW()),
                                                                                ('user3', 'test_password_3', 'user3@gmail.com', 'ACTIVE', NOW()),
                                                                                ('user4', 'test_password_4', 'user4@gmail.com', 'ACTIVE', NOW());