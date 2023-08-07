CREATE TABLE IF NOT EXISTS users (
                       create_date TIMESTAMP(6),
                       last_modified_date TIMESTAMP(6),
                       user_id BIGINT AUTO_INCREMENT,
                       nickname VARCHAR(255) NOT NULL,
                       profile_image_url VARCHAR(255),
                       PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS meeting (
                         date DATE,
                         time TIME(6),
                         create_date TIMESTAMP(6),
                         last_modified_date TIMESTAMP(6),
                         meeting_id BIGINT AUTO_INCREMENT,
                         meeting_status VARCHAR(255) CHECK (meeting_status IN ('', '', '', '')),
                         memo VARCHAR(255),
                         place VARCHAR(255) NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         PRIMARY KEY (meeting_id)
);

CREATE TABLE IF NOT EXISTS participation (
                               create_date TIMESTAMP(6),
                               last_modified_date TIMESTAMP(6),
                               meeting_id BIGINT,
                               participation_id BIGINT AUTO_INCREMENT,
                               user_id BIGINT,
                               button_authority VARCHAR(255) CHECK (button_authority IN ('', '')),
                               meeting_authority VARCHAR(255) CHECK (meeting_authority IN ('', '')),
                               participation_meeting_status VARCHAR(255) CHECK (participation_meeting_status IN ('', '')),
                               PRIMARY KEY (participation_id)
);

CREATE TABLE IF NOT EXISTS feed (
                      create_date TIMESTAMP(6),
                      feed_id BIGINT AUTO_INCREMENT,
                      last_modified_date TIMESTAMP(6),
                      participation_id BIGINT UNIQUE,
                      feed_image_url VARCHAR(255) NOT NULL,
                      PRIMARY KEY (feed_id)
);

CREATE TABLE IF NOT EXISTS report (
                        create_date TIMESTAMP(6),
                        feed_id BIGINT,
                        last_modified_date TIMESTAMP(6),
                        participation_id BIGINT UNIQUE,
                        report_id BIGINT AUTO_INCREMENT,
                        PRIMARY KEY (report_id)
);

CREATE TABLE IF NOT EXISTS fcm (
                          create_date TIMESTAMP(6),
                          last_modified_date TIMESTAMP(6),
                          fcm_id BIGINT AUTO_INCREMENT,
                          user_id BIGINT,
                          fcm_token VARCHAR(255) NOT NULL,
                          PRIMARY KEY (fcm_id)
);
