import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
	private static RentalSystem instance;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    private RentalSystem() {
        loadData();
    }
    

    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }
    
    public void saveVehicle(Vehicle vehicle) {
        try (BufferedWriter writer= new BufferedWriter(new FileWriter("vehicles.txt", true))) {
        	writer.append(vehicle.getInfo()+"\n");
        	writer.close();
        		
    } catch (IOException e) {
    	System.out.println("**ERROR** in adding vehicle: " + e.getMessage());
        }
    }

    public boolean addVehicle(Vehicle vehicle) {
    	
    	if(findVehicleByPlate(vehicle.getLicensePlate())!=null) {
    		System.out.println("The given license plate:"+ vehicle.getLicensePlate() + ",is already registered in the system");
    		return false;
    	}
        vehicles.add(vehicle);
        this.saveVehicle(vehicle);
		return true;
    }

    public void saveCustomer(Customer customer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt", true))){
        	writer.append(customer.toString()+"\n");
        	writer.close();
            
        } catch (IOException e) {
        	System.out.println("**ERROR** in adding customer: " + e.getMessage());        
        	}
    }
    
    public boolean addCustomer(Customer customer) {
    	
    	if(findCustomerById(customer.getCustomerId())!=null) {
    		System.out.println("The given customer id:"+ customer.getCustomerId() + ",is already registered in the system");
    		return false;
    	}
        customers.add(customer);
        this.saveCustomer(customer);
        return true;
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
        	
            writer.append((record.toString()+"\n"));
            writer.close();
        } catch (IOException e) {
        	System.out.println("**ERROR** in ading record: " + e.getMessage());        
        	}
    }
    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRecords();
    }
    
private void loadVehicles() {
	try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] vehicleData = line.split(" \\| ");
            if (vehicleData.length < 6) continue;
            String licensePlate=vehicleData[0];
            String make=vehicleData[1].trim();
            String model=vehicleData[2].trim();
            int year=Integer.parseInt(vehicleData[3].trim());
            Vehicle.VehicleStatus status=Vehicle.VehicleStatus.valueOf(vehicleData[4]);
            
            Vehicle vehicle;
            if (line.contains("Seats: ")) {
                // For Car class
                int seats = Integer.parseInt(vehicleData[5].split(": ")[1]);
                vehicle = new Car(make, model, year, seats);
            }
            else if (line.contains("Sidecar: ")) {
                // For Motorcycle class
                boolean sidecar = vehicleData[6].split(": ")[1].equals("Yes");
                vehicle = new Motorcycle(make, model, year, sidecar);
            }
            else if (line.contains("Seats: ") && line.contains("Horsepower: ")) {
                // For SportCar class
                int seats = Integer.parseInt(vehicleData[6].split(": ")[1]);
                int horsepower = Integer.parseInt(vehicleData[7].split(": ")[1]);
                boolean turbo = vehicleData[7].split(": ")[8].equals("Yes");
                vehicle = new SportCar(make, model, year, seats, horsepower, turbo);
            }
            else if (line.contains("Cargo Capacity: ")) {
                // For Truck calss
                double capacity = Double.parseDouble(vehicleData[5].split(": ")[0]);
                vehicle = new Truck(make, model, year, capacity);
            }
                
                else {
                	continue;
                }
                vehicle.setLicensePlate(licensePlate);
                vehicle.setStatus(status);
                vehicles.add(vehicle);
                
                }
    } catch (IOException e) {
        System.out.println("**GIVEN VEHICLE NOT FOUND**");
        e.printStackTrace();
    }
	
}

private void loadCustomers() {
	try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] customerData = line.split(" \\| ");
            try {
              if (customerData.length < 6) continue;
              int customerID=Integer.parseInt(customerData[1].trim());
              String customerName=customerData[2].trim();
              customers.add(new Customer(customerID,customerName));
            
        } catch (Exception e) {
            System.err.println("Failed to parse customer line: " + line);
        }
        }
            
    } catch (IOException e) {
        System.out.println("**GIVEN CUSTOMER NOT FOUND**");
    }
}

private void loadRecords() {
	try (BufferedReader reader = new BufferedReader(new FileReader("rental_records.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] recordData = line.split(",");
            if(recordData.length<6)continue;
            
            String licensePlate = recordData[1].trim();
            Vehicle vehicle = findVehicleByPlate(licensePlate);
            if (vehicle == null) continue;
            
            int customerId = Integer.parseInt(recordData[2].trim());
            Customer customer = findCustomerById(customerId);
            if (customer == null) continue;
            
            
            LocalDate date = LocalDate.parse(recordData[3].trim());
            
            double amount = Double.parseDouble(recordData[4].trim());
            String transactionType = recordData[5].trim();
            
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, transactionType);
            rentalHistory.addRecord(record);           
} 
	}catch(IOException e) {
		System.out.println("**GIVEN RECORDS WERE NOT FOUND**");
		 e.printStackTrace();
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