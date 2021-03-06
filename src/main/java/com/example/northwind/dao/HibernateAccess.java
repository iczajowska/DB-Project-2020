package com.example.northwind.dao;

import com.example.northwind.model.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

// this is where the most will need to be done. This is where we'll be implementing add the DB access methods

@Repository("HibernateAccess") // repository here meaning a class that stored data in a DB
public class HibernateAccess implements NorthwindDao {

    @PersistenceContext
    private EntityManager em;


    // ==========  PRODUCT  ========== //

    @Override
    @Transactional
    public int addProduct(Product product, int category_id) {
        Category category = em.find(Category.class, category_id);
        em.persist(product);
        category.addProduct(product);
        return 0;
    }

    @Override
    @Transactional
    public List<Product> getAllProducts() { return em.createQuery("FROM Product").getResultList(); }

    @Override
    @Transactional
    public Product getProductById(int product_id) {
        return em.find(Product.class,product_id);
    }

    @Override
    @Transactional
    public void deleteProductById(int product_id) {
        Product prod = em.find(Product.class, product_id);
        int category_id = prod.getCategory();
        Category cat = em.find(Category.class, category_id);
        cat.removeProduct(prod);
        em.remove(prod);
    }


    // ==========  CATEGORY  ========== //

    @Override
    @Transactional
    public int addCategory(Category category) {
        em.persist(category);
        return 0;
    }

    @Override
    @Transactional
    public List<Category> getAllCategories() {
        return em.createQuery("FROM Category ").getResultList();
    }

    @Override
    @Transactional
    public Category getCategoryByID(int category_id) {
        return em.find(Category.class, category_id);
    }

    @Override
    @Transactional
    public void deleteCategoryById(int category_id) { //orphan removal set for products
        Category category = em.find(Category.class, category_id);
        em.remove(category);
    }


    // ==========  CUSTOMER  ========== //

    @Override
    @Transactional
    public int addCustomer(Customer customer) {
        em.persist(customer);
        return 0;
    }

    @Override
    @Transactional
    public List<Customer> getAllCustomers() { return em.createQuery("FROM Customer").getResultList(); }

    @Override
    @Transactional
    public Customer getCustomerById(int customer_id) {
        return em.find(Customer.class,customer_id);
    }



    // ==========  SHIPPER  ========== //

    @Override
    @Transactional
    public int addShipper(Shipper shipper) {
        em.persist(shipper);
        return 0;
    }

    @Override
    @Transactional
    public List<Shipper> getAllShippers() {
        return em.createQuery("FROM Shipper").getResultList();
    }

    @Override
    @Transactional
    public Shipper getShipperById(int shipper_id) {
        return em.find(Shipper.class, shipper_id);
    }



    // ==========  ORDER  ========== //

    @Override
    @Transactional
    public int addOrder(Order o) {
        em.persist(o);
        return 0;
    }

    @Override
    @Transactional
    public List<Order> getAllOrders() {
        return em.createQuery("FROM Order").getResultList();
    }

    @Override
    @Transactional
    public Order getOrder(int order_id){
        return em.find(Order.class, order_id);
    }

    @Override
    @Transactional
    public int cancelOrder(int order_id) {
        em.find(Order.class, order_id).setCancelled(true);
        List<OrderDetails> details = getOrderDetailsByOrderId(order_id);
        for (OrderDetails od: details) {
            Product p = em.find(Product.class, od.getProduct());
            p.setUnitInStock(p.getUnitInStock() + od.getQuantity());
            em.remove(od);
        }
        return 0;
    }


    // ======  ORDER DETAILS  ====== //
    @Override
    @Transactional
    public List<OrderDetails> getAllOrderDetails() {
        return em.createQuery("FROM OrderDetails").getResultList();
    }

    @Override
    @Transactional
    public List<OrderDetails> getOrderDetailsByOrderId(int order_id) {
        return em.createQuery("from OrderDetails as OD where OD.order.id='"+order_id+"'",OrderDetails.class).getResultList();
    }

    @Override
    @Transactional
    public int addOrderDetails(OrderDetails d) {
        em.persist(d);
        Product p = em.find(Product.class, d.getProduct());
        p.setUnitsOnOrder(p.getUnitsOnOrder() + d.getQuantity());
        p.setUnitInStock(p.getUnitInStock() - d.getQuantity());
        return 0;
    }

}
