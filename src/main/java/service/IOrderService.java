package service;

import model.Order;
import model.Response;

public interface IOrderService {
    Response addNewOrder(Order order);
    Response getOrder(int id);
    Order[] getAllOrders(String date);
    Response updateOrder(int id);
}
