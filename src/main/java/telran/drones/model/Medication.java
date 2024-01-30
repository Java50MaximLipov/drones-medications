package telran.drones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "medications")
@Getter
@ToString
public class Medication {

	@Id
	String code;

	@Column(nullable = false)
	String name;

	@Column(nullable = false)
	int weight;

}