package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.UserRepository;
import com.kenzie.appserver.repositories.model.UserRecord;
import com.kenzie.capstone.service.client.LambdaServiceClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
    private UserRepository userRepository;
    private LambdaServiceClient lambdaServiceClient;

    public UserService(UserRepository userRepository, LambdaServiceClient lambdaServiceClient) {
        this.userRepository = userRepository;
        this.lambdaServiceClient = lambdaServiceClient;
    }

    public UserRecord findUserById(String id) {
        Optional<UserRecord> user = userRepository.findById(id);

        if (!user.isPresent() || user.isEmpty()) return null;

        UserRecord userRecord = user.get();

        UserRecord ur = new UserRecord(userRecord.getUserID(), userRecord.getUserName(), userRecord.getPassword(),
                userRecord.getEventsList(), userRecord.getEmail(), userRecord.getFirstName(), userRecord.getLastName(),
                userRecord.getNotifications(), userRecord.getUserType());

        return ur;
    }

    public List<UserRecord> getAllUsers() {
        List<UserRecord> users = new ArrayList<>();
        Iterable<UserRecord> pulledUsers = userRepository.findAll();

        if (pulledUsers == null) return null;

        for (UserRecord ur : pulledUsers) {
            UserRecord currentRecord = new UserRecord(ur.getUserID(), ur.getUserName(), ur.getPassword(),
                    ur.getEventsList(), ur.getEmail(), ur.getFirstName(), ur.getLastName(),
                    ur.getNotifications(), ur.getUserType());

            users.add(currentRecord);
        }

        return users;
    }

    public UserRecord addNewUser(String userName, String password, String email, String firstName, String lastName, String userType) {
        UserRecord ur = new UserRecord(userName, password, email, firstName, lastName, userType);

        userRepository.save(ur);

        return ur;
    }
}
