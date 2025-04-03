import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
	private static RentalSystem instance;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }
    
    public void saveVehicle(Vehicle vehicle) {
        try (BufferedWriter writer= new BufferedWriter(new FileWriter("vehicles.txt", true))) {
        	writer.append(vehicle.getLicensePlate() + "," + 
                          vehicle.getMake() + "," + 
        			      vehicle.getModel() + "," + 
                          vehicle.getYear());
        	writer.close();
        		
    } catch (IOException e) {
    	System.out.println("**ERROR** in adding vehicle: " + e.getMessage());
        }
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
    }

    public void saveCustomer(Customer customer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt", true))){
        	writer.append(customer.getCustomerId()+","+
                          customer.getCustomerName());
        	writer.close();
            
        } catch (IOException e) {
        	System.out.println("**ERROR** in adding customer: " + e.getMessage());        
        	}
    }
    
    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
            RentalRecord record=new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            saveRecord(record);
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
        
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, extraFees, "RETURN"));
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            saveRecord(record);
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }   
    
    public void saveRecord(RentalRecord record) {
        try (BufferedWriter writer = new BufferedWriter (new FileWriter("rental_records.txt", true))) {
        	
            writer.append((record.getVehicle().getLicensePlate() + "," + 
                           record.getCustomer().getCustomerId() + "," + 
            		       record.getRecordDate() + "," + 
                           record.getTotalAmount() + "," +
            		       record.getRecordType()));
            writer.close();
        } catch (IOException e) {
        	System.out.println("**ERROR** in ading record: " + e.getMessage());        
        	}
    }

    public void displayAvailableVehicles() {
    	System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
    	System.out.println("---------------------------------------------------------------------------------");
    	 
        for (Vehicle v : vehicles) {
            if (v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                System.out.println("|     " + (v instanceof Car ? "Car          " : "Motorcycle   ") + "|\t" + v.getLicensePlate() + "\t|\t" + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|\t");
            }
        }
        System.out.println();
    }
    
    public void displayAllVehicles() {
        for (Vehicle v : vehicles) {
            System.out.println("  " + v.getInfo());
        }
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }

    public Customer findCustomerByName(String name) {
        for (Customer c : customers)
            if (c.getCustomerName().equalsIgnoreCase(name))
                return c;
        return null;
    }
}