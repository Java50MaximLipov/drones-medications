delete from drones;
delete from medications;
delete from drone_models;
delete from event_logs;
insert into drone_models (model_name, weight) values
('Lightweight', 100),
('Middleweight', 300),
('Cruiserweight', 400),
('Heavyweight', 500);
insert into drones (drone_number, model_name,  battery_capacity, state) 
	values
		('Drone-1', 'Middleweight', 100, 'IDLE'),
		('Drone-2', 'Middleweight', 100, 'IDLE'),
		('Drone-3', 'Middleweight', 100, 'IDLE'),
		('Drone-4', 'Lightweight', 100, 'IDLE'),
		('Drone-5', 'Lightweight', 100, 'IDLE'),
		('Drone-6', 'Lightweight', 100, 'IDLE'),
		('Drone-7', 'Cruiserweight', 100, 'IDLE'),
		('Drone-8', 'Cruiserweight', 100, 'IDLE'),
		('Drone-9', 'Heavyweight', 100, 'IDLE'),
		('Drone-10', 'Heavyweight', 100, 'IDLE');
insert into medications (code, name, weight)
	values 
		('MED_1', 'Medication-1', 200),
		('MED_2', 'Medication-2', 350),
		('MED_3', 'Medication-3', 90);
			
		