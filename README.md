# event-service

Service description
Service lets users create events and make them accessible to others. Others can view events by city and by date. There are also private events that are only accessible to persons who are invited. Registered users can also register their attendance for the event. Application uses external API to check whether entered city in country exists and gives user list of countries and cities to choose from. There are also additional information available with administrator links.

High level architecture
Event service is one microservice that is connected to an external api for location verification and passing to user and database that stores all data.

More information: https://docs.google.com/document/d/1g6rLOSncxhZ9xmqjVD1-noPc_7ShZnQZZaI4O5SeIGY/edit?usp=sharing
