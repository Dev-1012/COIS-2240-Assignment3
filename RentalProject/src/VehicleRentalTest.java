import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;

class VehicleRentalTest {
	@Test
    public void testLicensePlateValidation() {
        
        Vehicle v1 = new Car();
        Vehicle v2 = new Car();
        Vehicle v3 = new Car();

        assertDoesNotThrow(() -> v1.setLicensePlate("AAA100"));
        assertEquals("AAA100", v1.getLicensePlate());

        assertDoesNotThrow(() -> v2.setLicensePlate("ABC567"));
        assertEquals("ABC567", v2.getLicensePlate());

        assertDoesNotThrow(() -> v3.setLicensePlate("ZZZ999"));
        assertEquals("ZZZ999", v3.getLicensePlate());

       
        Vehicle invalidVehicle = new Car();

        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate("ZZZ99"));
    }
	private RentalSystem rentalSystem;
    private Car car;
    private Customer customer;

    @BeforeEach
    public void setUp() {
        rentalSystem = RentalSystem.getInstance();
        car = new Car("Hyundai", "Creta", 2025, 5);
        car.setLicensePlate("DEL111");
        customer = new Customer(010, "Dev");

        rentalSystem.addVehicle(car);
        rentalSystem.addCustomer(customer);
    }

    @Test
    public void testRentAndReturnVehicle() {
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, car.getStatus());

        // For rent
        rentalSystem.rentVehicle(car, customer, LocalDate.now(), 300);
        assertEquals(Vehicle.VehicleStatus.RENTED, car.getStatus());

        rentalSystem.rentVehicle(car, customer, LocalDate.now(), 300);
        assertEquals(Vehicle.VehicleStatus.RENTED, car.getStatus(), "VEHICLE STATUS SHOULD REMAIN RENTED");
        
        //For return
        rentalSystem.returnVehicle(car, customer, LocalDate.now(), 75);
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, car.getStatus());

        rentalSystem.returnVehicle(car, customer, LocalDate.now(), 75);
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, car.getStatus(), "VEHICLE STATUS SHOULD REMAIN AVAILABLE");

    }
}



