import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}