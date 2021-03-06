package org.jamp.example;

import java.util.ArrayList;
import java.util.List;
import org.jamp.JampProxyCreator;
import org.jamp.MQMessageSender;
import org.jamp.example.model.AddressBook;
import org.jamp.example.model.Employee;
import org.jamp.example.model.EmployeeService;
import org.jamp.impl.JampProxyCreatorImpl;

@SuppressWarnings("nls")
public class EmployeeServiceStompClient {
    
    public static void main (String [] args) throws Exception {
        
        
        MQMessageSender sender = org.jamp.Factory.factory().createMQMessageSender("stomp://localhost:6666/foo", 
                "rick", "rick");
        
        JampProxyCreator proxy = new JampProxyCreatorImpl( sender );
        
        EmployeeService service = (EmployeeService) proxy.createProxy(EmployeeService.class);
        
        List<AddressBook> books = new ArrayList<AddressBook>();
        books.add(new AddressBook("a"));
        books.add(new AddressBook("b"));
        
        service.addEmployee(new Employee("Rick Hightower", "5205551212", books), 7, 9.99f, 8, "love's love but no love says");
        service.addEmployee(new Employee("Rick1", "5205551212", books), 7, 9.99f, 8, "rocket");
        service.addEmployee(new Employee("Rick2", "5205551213", books), 7, 9.99f, 8, "socket");
        service.addEmployee(new Employee("Rick3", "5205551214", books), 7, 9.99f, 8, "nine");
        service.addEmployee(new Employee("Rick4", "5205551215", books), 7, 9.99f, 8, "ten");
        service.addEmployee(new Employee("Rick5", "5205551216", books), 7, 9.99f, 8, "eleven");
        service.addEmployee(new Employee("Rick6", "5205551217", books), 7, 9.99f, 8, "twelve");

    }


}
