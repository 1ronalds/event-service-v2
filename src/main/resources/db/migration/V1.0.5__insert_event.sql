use eventdb;

INSERT INTO event (event_id, event_private_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count, event_type)
VALUE ('1', NULL, 'Bicycling contest', 'A contest of bicycling free to watch and participate', 'Latvia', 'Riga', '2', '300', '2022-12-08 13:00:00', '1', '1');
INSERT INTO event (event_id, event_private_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count, event_type)
VALUES ('2', '1', 'Theater', 'Everyone will be amazed watching this theatre', 'Latvia', 'Venstspils', '3', '50', '2022-12-04 15:30:00', '1', '2');
INSERT INTO event (event_id, event_private_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count, event_type)
VALUES ('3', NULL, 'Marathon', 'Running is good for your health, so join our 7km marathon', 'Lithuania', 'Vilnius', '4', '1000', '2022-12-01 10:30:00', '2', '1');