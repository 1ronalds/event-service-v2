use eventdb;

DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS user;


CREATE TABLE type(
type_id bigint(20) NOT NULL AUTO_INCREMENT,
type varchar(7) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
PRIMARY KEY(type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE role(
role_id bigint(20) NOT NULL AUTO_INCREMENT,
role varchar(7) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
PRIMARY KEY(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE user (
user_id bigint(20) NOT NULL AUTO_INCREMENT,
username varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL UNIQUE,
email varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL UNIQUE,
password varchar(20)CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
name varchar(20)CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
surname varchar(20)CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
role bigint(20) NOT NULL,
PRIMARY KEY (user_id),
FOREIGN KEY (role) REFERENCES role(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE event (
event_id bigint(20) NOT NULL AUTO_INCREMENT,
event_title varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
event_description varchar(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
event_location_country varchar(25)  CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
event_location_city varchar(25) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
organiser_id bigint(20) NOT NULL,
max_attendance int NOT NULL,
event_datetime datetime NOT NULL,
attendee_count int DEFAULT '0',
type bigint(20) NOT NULL,
PRIMARY KEY (event_id),
FOREIGN KEY (organiser_id) REFERENCES user(user_id),
FOREIGN KEY (type) REFERENCES type(type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE attendance (
attendance_id bigint(20) NOT NULL AUTO_INCREMENT,
event_id bigint(20) NOT NULL,
user_id bigint NOT NULL,
PRIMARY KEY (attendance_id),
FOREIGN KEY (event_id) REFERENCES event(event_id),
FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;