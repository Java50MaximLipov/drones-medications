# Assumptions
1. Introducing intermediate Drone's states related to unit time of periodic task
2. Battery capacity is changing on 2% per time unit.
3.  Total for full cycle on 22% (11 time units)
4.  From LOADING to LOADED, LOADED to DELIVERING, DELIVERED to RETURNING required one time unit (time unit is predefined and may be configured)
5.  From DELIVERING to DELIVERED, RETURNING to IDLE - 4 time units
6. To keep things much simplier there are introduced intermediate states between DELIVERING and DELIVERED, and between RETURNING and IDLE that has allowed considering all moves per one time unit with appropriated logging
7. Battery is charging in IDLE state with the same principle (2% per time unit) 
# Running instructions
1. Download ZIP from GitHub
2. Unzip
3. Enter folder with unzipped project
4. Run command maven wrapper mvnw package. All Unit Tests should be performed and as a result there will be created JAR file drones-medications-0.0.1.jar
5. Run command java -jar trget/drones-medications-0.0.1.jar. As a result the application will start on the port 8080
6. By using Postman or any other Restful client there may be performed a sanity integration test according to the API
# API
1. /drones - POST method with DronDto DTO object
2. TODO
# SQL script for initial DB population
1. TODO script for fleet of 10 drones, ...
