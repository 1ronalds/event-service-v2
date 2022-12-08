use eventdb;

INSERT INTO event (event_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count)
VALUE ('1', 'Bicycling contest', 'A contest of bicycling free to watch and participate', 'Latvia', 'Riga', '2', '300', '2022-12-08 13:00:00', '1');
INSERT INTO event (event_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count)
VALUES ('2', 'Theater', 'Everyone will be amazed watching this theatre', 'Latvia', 'Venstspils', '3', '50', '2022-12-04 15:30:00', '1');
INSERT INTO event (event_id, event_title, event_description, event_location_country, event_location_city, organiser_id, max_attendance, event_datetime, attendee_count)
VALUES ('3', 'Marathon', 'Running is good for your health, so join our 7km marathon', 'Lithuania', 'Vilnius', '4', '1000', '2022-12-01 10:30:00', '2');