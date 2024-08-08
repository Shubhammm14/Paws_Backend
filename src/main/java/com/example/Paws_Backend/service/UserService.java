package com.example.Paws_Backend.service;


import com.example.Paws_Backend.exception.UserExcepition;
import com.example.Paws_Backend.model.PurchaseOrder;
import com.example.Paws_Backend.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    public User findUserById(Long userId);
    public User findUserByEmail(String email);

    List<PurchaseOrder> getOrdersNeedingApproval(Long sellerId);


    public User updateUser(User user, Long userId) throws UserExcepition;
    public User findUserByJwt( String token);
}
