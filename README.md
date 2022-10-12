HTTP methods description:
 * /order/add - POST - Adds an order
 * /order/{id}/get - GET - Get order by 'id' in the URI
 * /order/{time}/getAll - GET - Get all orders by 'time' in the URI
 * /order/{id}/update - PUT - Update the with 'id' in the URI
 
 HTTP response status codes:
 Client error responses:
 * 433 - Invalid order format
 
 Server error responses:
 * 519 - The Order already exist
 * 520 - The order doesn't exist
 * 521 - The order can't be updated
 * 522 - There is no order by this id
