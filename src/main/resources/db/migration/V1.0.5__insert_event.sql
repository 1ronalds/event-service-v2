use eventdb;

INSERT INTO event (event_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count, type)
VALUE ('1', 'Bicycling contest', 'A contest of bicycling free to watch and participate', 'Latvia', 'Riga', '2', '300', '2022-12-08 13:00:00', '1', '1');
INSERT INTO event (event_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count, type)
VALUES ('2', 'Theater', 'Everyone will be amazed watching this theatre', 'Latvia', 'Venstspils', '3', '50', '2022-12-04 15:30:00', '1', '2');
INSERT INTO event (event_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count, type)
VALUES ('3', 'Marathon', 'Running is good for your health, so join our 7km marathon', 'Lithuania', 'Vilnius', '4', '1000', '2022-12-01 10:30:00', '2', '1');
INSERT INTO event (event_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count, type)
VALUES ('4', 'Yoga', 'Come, join us in a group yoga session.', 'Latvia', 'Venstspils', '4', '200', '2022-12-01 10:30:00', '0', '1');
INSERT INTO event (event_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count, type)
VALUES ('5', 'Movie night', 'Join us in watching a christmas movie', 'Latvia', 'Riga', '4', '500', '2022-12-01 10:30:00', '0', '1');
INSERT INTO event (event_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count, type)
VALUES ('6', 'Kebab eating contest', 'Prove to everyone once and for all that you are the best kebab eater in Lithuania!', 'Lithuania', 'Kaunas', '4', '500', '2022-12-01 10:30:00', '0', '2');