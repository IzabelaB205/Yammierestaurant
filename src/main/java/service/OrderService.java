package service;

import model.Order;
import model.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/***
 * HTTP methods description:
 * /order/add	            POST    Adds an order
 * /order/{id}/get	        GET	    Get order by 'id' in the URI
 * /order/{time}/getAll	    GET     Get all orders by 'time' in the URI
 * /order/{id}/update	    PUT	    Update the with 'id' in the URI
 *
 * HTTP response status codes:
 * Client error responses:
 * 433 - Invalid order format
 *
 * Server error responses:
 * 519 - The Order already exist
 * 520 - The order doesn't exist
 * 521 - The order can't be updated
 * 522 - There is no order by this id
 */

@Path("/order")
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public class OrderService implements IOrderService{

    private Map<Integer, Order> orders;

    public OrderService(){
        orders = new HashMap<>();
    }

    @Override
    @POST
    @Path("/add")
    public Response addNewOrder(Order order) {
        Response response = new Response();

        //The order already exist in the system
        if(orders.get(order.getId()) != null) {
            response.setCode(519);
            response.setStatus(false);
            response.setMessage("The order already exist");
        }
        else if(!isValidOrder(order)){
            response.setCode(433);
            response.setStatus(false);
            response.setMessage("Invalid order format");
        }
        else {
            orders.put(order.getId(), order);
            response.setCode(200);
            response.setStatus(true);
            response.setMessage("Order added successfully");
        }
        return response;
    }

    @Override
    @GET
    @Path("/{id}/get")
    public Response getOrder(@PathParam("id") int id) {
        Response response = new Response();
        Order order = orders.get(id);

        if(order == null) {
            response.setCode(522);
            response.setStatus(false);
            response.setMessage("There is no order by this id");
        }
        else {
            response.setCode(200);
            response.setStatus(true);
            response.setMessage("Return order successfully");
            response.setData(order);
        }
        return response;
    }

    @Override
    @GET
    @Path("/{time}/getAll")
    public Order[] getAllOrders(@PathParam("time") String time) {
        List<Order> ordersList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        switch(time.toLowerCase()) {
            case "day":
                //create a last day date from current date
                LocalDate currentDateMinusOneDay = currentDate.minusDays(1);
                ordersList = getOrdersListByDate(currentDate, currentDateMinusOneDay);
                break;

            case "week":
                //create a last week date from current date
                LocalDate currentDateMinusOneWeek = currentDate.minusWeeks(1);
                ordersList = getOrdersListByDate(currentDate, currentDateMinusOneWeek);
                break;

            case "month":
                //create a last month date from current date
                LocalDate currentDateMinusOneMonth = currentDate.minusMonths(1);
                ordersList = getOrdersListByDate(currentDate, currentDateMinusOneMonth);
                break;
        }

        //convert ArrayList to order type Array
        Order[] result = new Order[ordersList.size()];

        for(int i = 0; i < ordersList.size(); i++) {
            result[i] = ordersList.get(i);
        }

        return result;
    }

    @Override
    @PUT
    @Path("/{id}/update")
    public Response updateOrder(@PathParam("id") int id) {
        Response response = new Response();
        Order order = orders.get(id);

        //The order doesn't exist
        if(order == null) {
            response.setCode(520);
            response.setStatus(false);
            response.setMessage("The order doesn't exist");
        }
        else {
            //Check if the order can be updated
            if(isOrderCanBeUpdated(id)) {
                LocalDateTime localDateTime = LocalDateTime.now();
                DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String[] dateTimeArray = localDateTime.format(dateTimeFormat).split(" ");

                order = orders.remove(order.getId());
                order.setDate(dateTimeArray[0]);
                order.setTime(dateTimeArray[1]);
                orders.put(id, order);

                response.setCode(200);
                response.setStatus(true);
                response.setMessage("Order updated successfully");
            }
            else {
                response.setCode(521);
                response.setStatus(false);
                response.setMessage("The order can't be updated");
            }
        }

        return response;
    }

    //This method checks the order's fields validation
    private boolean isValidOrder(Order order) {
        if(order == null ||
                Integer.valueOf(order.getId()) == null ||
                order.getTime() == null ||
                order.getDate() == null
        ) {
            return false;
        }

        return isValidTime(order.getTime()) &&
                isValidDate(order.getDate());
    }

    //Check date validation
    private boolean isValidDate(String orderDate) {
        boolean isDate = true;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        try
        {
            dateFormat.parse(orderDate);
        } catch (ParseException e) {
            isDate = false;
        }

        return isDate;
    }

    //Check time validation in 24-hour format
    private boolean isValidTime(String orderTime) {
        return orderTime.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-9][0-9]");
    }

    private boolean isOrderCanBeUpdated(int id) {
        boolean isOrderUpdated = true;
        Order order = orders.get(id);
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime orderLocalDateTime = LocalDateTime.parse(order.getDate() + " " + order.getTime(), dateTimeFormat);

        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        LocalDateTime orderDateTimePlus15Minutes = orderLocalDateTime.plusMinutes(15);

        if(currentLocalDateTime.isAfter(orderDateTimePlus15Minutes)) {
            isOrderUpdated = false;
        }

        return isOrderUpdated;
    }

    private List<Order> getOrdersListByDate(LocalDate currentDate, LocalDate lastDate) {
        List<Order> ordersId = new ArrayList<>(orders.values());
        List<Order> ordersList = new ArrayList<>();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (Order order: ordersId) {
            LocalDate orderLocalDate = LocalDate.parse(order.getDate(), dateFormat);

            //Check if the order date is within the range of the current date and the desired date
            if((orderLocalDate.isBefore(currentDate) || orderLocalDate.isEqual(currentDate)) &&
                    (orderLocalDate.isAfter(lastDate) || orderLocalDate.isEqual(lastDate))) {
                ordersList.add(order);
            }
        }

        return ordersList;
    }
}
