package service;

import junit.framework.TestCase;
import model.Order;
import model.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class OrderServiceTest extends TestCase {
    OrderService orderService;
    Order order;

    @Before
    public void setUp() {
        orderService = new OrderService();

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String[] dateTimeArray = dateTime.format(dateTimeFormat).split(" ");

        order = new Order();
        order.setId(1);
        order.setDate(dateTimeArray[0]);
        order.setTime(dateTimeArray[1]);
    }

    @Test
    public void testTimeValidation() {
        String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-9][0-9]";

        Assert.assertTrue("11:45:00".matches(regex));
        Assert.assertTrue("23:45:99".matches(regex));
        Assert.assertFalse("11:60:99".matches(regex));
        Assert.assertFalse("11:45:999".matches(regex));
        Assert.assertFalse("F1:60:%9".matches(regex));
        Assert.assertFalse("".matches(regex));
    }

    @Test
    public void testDateValidation() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        Assert.assertNotNull(dateFormat.parse("24-05-1993"));
        Assert.assertThrows(ParseException.class, () -> dateFormat.parse("32-05-1993"));
        Assert.assertThrows(ParseException.class, () -> dateFormat.parse("aD-15-1%93"));
        Assert.assertThrows(ParseException.class, () -> dateFormat.parse("20/05/1993"));
    }

    @Test
    public void testValidOrderAddition() {
        Response response = orderService.addNewOrder(order);

        Assert.assertEquals(200, response.getCode());
        Assert.assertTrue(response.isStatus());
        Assert.assertEquals("Order added successfully", response.getMessage());
        Assert.assertNull(response.getData());
    }

    @Test
    public void testInvalidOrderAddition() {
        order.setId(1);
        order.setTime("a1:ff:%0");
        order.setDate("1210-2022");

        Response response = orderService.addNewOrder(order);

        Assert.assertEquals(433, response.getCode());
        Assert.assertFalse(response.isStatus());
        Assert.assertEquals("Invalid order format", response.getMessage());
        Assert.assertNull(response.getData());
    }

    @Test
    public void testExistedOrderAddition() {
        orderService.addNewOrder(order);

        //Trying to add an existed order  - server should return status code 519
        Response response = orderService.addNewOrder(order);

        Assert.assertEquals(519, response.getCode());
        Assert.assertFalse(response.isStatus());
        Assert.assertEquals("The order already exist", response.getMessage());
        Assert.assertNull(response.getData());
    }

    @Test
    public void testGetOfValidOrder() {
        orderService.addNewOrder(order);
        Response response = orderService.getOrder(order.getId());

        Assert.assertEquals(200, response.getCode());
        Assert.assertTrue(response.isStatus());
        Assert.assertEquals("Return order successfully", response.getMessage());
        Assert.assertNotNull(response.getData());
    }

    @Test
    public void testGetOfNotExistedOrder() {
        Response response = orderService.getOrder(1);

        Assert.assertEquals(522, response.getCode());
        Assert.assertFalse(response.isStatus());
        Assert.assertEquals("There is no order by this id", response.getMessage());
        Assert.assertNull(response.getData());
    }

    @Test
    public void testGetOrdersFromLastDay() {
        LocalDate currentDate = LocalDate.now();
        LocalDate currentDateMinusOneDay = currentDate.minusDays(1);
        String date = convertLocalDateToString(currentDateMinusOneDay);

        LocalTime currentTime = LocalTime.now();
        String time = convertLocalTimeToString(currentTime);

        order.setId(1);
        order.setDate(date);
        order.setTime(time);

        //order with date from more than last month
        LocalDate currentDateMinusThreeDays = currentDate.minusDays(3);
        String passedDate = convertLocalDateToString(currentDateMinusThreeDays);
        Order passedOrder = new Order();
        passedOrder.setId(1);
        passedOrder.setTime(time);
        passedOrder.setDate(passedDate);

        orderService.addNewOrder(order);
        orderService.addNewOrder(passedOrder);
        Order[] orders = orderService.getAllOrders("day"); // should have only one order

        Assert.assertNotNull(orders);
        Assert.assertEquals(1, orders.length);
        Assert.assertEquals(1, orders[0].getId());
        Assert.assertEquals(date, orders[0].getDate());
        Assert.assertEquals(time, orders[0].getTime());
    }

    @Test
    public void testGetOrdersFromLastWeek() {
        LocalDate currentDate = LocalDate.now();
        LocalDate currentDateMinusOneWeek = currentDate.minusDays(3);
        String date = convertLocalDateToString(currentDateMinusOneWeek);

        LocalTime currentTime = LocalTime.now();
        String time = convertLocalTimeToString(currentTime);

        //order with date from last week
        order.setId(1);
        order.setTime(time);
        order.setDate(date);

        //order with date from more than last week
        LocalDate currentDateMinusThreeWeeks = currentDate.minusWeeks(3);
        String passedDate = convertLocalDateToString(currentDateMinusThreeWeeks);
        Order passedOrder = new Order();
        passedOrder.setId(1);
        passedOrder.setTime(time);
        passedOrder.setDate(passedDate);

        orderService.addNewOrder(order);
        orderService.addNewOrder(passedOrder);
        Order[] orders = orderService.getAllOrders("week");

        Assert.assertNotNull(orders);
        Assert.assertEquals(1, orders.length);
        Assert.assertEquals(1, orders[0].getId());
        Assert.assertEquals(date, orders[0].getDate());
        Assert.assertEquals(time, orders[0].getTime());
    }

    @Test
    public void testGetOrdersFromLastMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate currentDateMinusDays = currentDate.minusDays(3);
        String date = convertLocalDateToString(currentDateMinusDays);

        LocalTime currentTime = LocalTime.now();
        String time = convertLocalTimeToString(currentTime);

        order.setId(1);
        order.setTime(time);
        order.setDate(date);

        //order with date from more than last month
        LocalDate currentDateMinusThreeMonths = currentDate.minusMonths(3);
        String passedDate = convertLocalDateToString(currentDateMinusThreeMonths);
        Order passedOrder = new Order();
        passedOrder.setId(1);
        passedOrder.setTime(time);
        passedOrder.setDate(passedDate);

        orderService.addNewOrder(order);
        orderService.addNewOrder(passedOrder);
        Order[] orders = orderService.getAllOrders("month");

        Assert.assertNotNull(orders);
        Assert.assertEquals(1, orders.length);
        Assert.assertEquals(1, orders[0].getId());
        Assert.assertEquals(date, orders[0].getDate());
        Assert.assertEquals(time, orders[0].getTime());
    }

    @Test
    public void testValidOrderUpdate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String[] dateTimeArray = localDateTime.format(dateTimeFormat).split(" ");

        order.setId(1);
        order.setDate(dateTimeArray[0]);
        order.setTime(dateTimeArray[1]);

        Response response = orderService.updateOrder(1);

        //Updating order that did not exist
        Assert.assertEquals(520, response.getCode());
        Assert.assertFalse(response.isStatus());
        Assert.assertEquals("The order doesn't exist", response.getMessage());
        Assert.assertNull(response.getData());

        orderService.addNewOrder(order);
        response = orderService.updateOrder(1);

        //Updating exist order
        Assert.assertEquals(200, response.getCode());
        Assert.assertTrue(response.isStatus());
        Assert.assertEquals("Order updated successfully", response.getMessage());
        Assert.assertNull(response.getData());
    }

    @Test
    public void testInValidOrderUpdate() {
        order.setId(1);
        order.setTime("10:30:00");
        order.setDate("12-10-2022");

        orderService.addNewOrder(order);
        Response response = orderService.updateOrder(1);

        Assert.assertEquals(521, response.getCode());
        Assert.assertFalse(response.isStatus());
        Assert.assertEquals("The order can't be updated", response.getMessage());
        Assert.assertNull(response.getData());
    }

    private String convertLocalDateToString(LocalDate date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(dateFormat);
    }

    private String convertLocalTimeToString(LocalTime time) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(timeFormat);
    }
}