package com.kenzie.appserver.controller;

import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.controller.model.EventCreateRequest;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.appserver.controller.model.*;


@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private QueryUtility queryUtility;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final MockNeat mockNeat = MockNeat.threadLocal();

    @BeforeAll
    public void setup(){
        queryUtility = new QueryUtility(mockMvc);
    }



    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);

        // When & Then
        queryUtility.userControllerClient.getUserById(userCreateRequest.getUserName())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userID").isNotEmpty());
    }
    @Test
    void getUserById_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        String userId = "non-existent-id";


        // When & Then
        queryUtility.userControllerClient.getUserById(userId)
                .andExpect(status().isNotFound());
    }

    @Test
    void addUser_UserAlreadyExists_ShouldReturnBadRequest() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);


        // When & Then
        queryUtility.userControllerClient.addUser(userCreateRequest)
                .andExpect(status().isBadRequest());
    }
    @Test
    void deleteUserById_ShouldDeleteUser() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);

        // When & Then
        queryUtility.userControllerClient.deleteUserById(userCreateRequest.getUserName())
                .andExpect(status().isOk());
    }

    @Test
    void addUser_ShouldCreateUser() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());


        // When & Then
        queryUtility.userControllerClient.addUser(userCreateRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userID").isNotEmpty());
    }
    @Test
    void loginUser_WhenValidCredentials_ShouldReturnUser() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);
        // When & Then
        queryUtility.userControllerClient.loginUser(userCreateRequest)
                .andExpect(status().isOk());
    }

    @Test
    void loginUser_PasswordIsIncorrect_ShouldReturnBadRequest() throws Exception{
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);
        userCreateRequest.setPassword("g");
        // When & Then
        queryUtility.userControllerClient.loginUser(userCreateRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_UserDoesNotExist_ShouldReturnBadRequest() throws Exception{
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        // When & Then
        queryUtility.userControllerClient.loginUser(userCreateRequest)
                .andExpect(status().isBadRequest());
    }


    @Test
    void joinEvent_WhenEventExists_ShouldJoinEvent() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);

        EventCreateRequest request = new EventCreateRequest();
        request.setEventSponsor("f");
        request.setLocation("f");
        request.setName("f");
        request.setEndTime(LocalDateTime.now().plusDays(3).toString());
        request.setStartTime(LocalDateTime.now().toString());
        ResultActions temp = queryUtility.eventControllerClient.addNewEvent(request);
        EventResponse eventResponse = objectMapper.readValue(temp.andReturn().getResponse().getContentAsString(), EventResponse.class);
        JoinEventRequest joinEventRequest = new JoinEventRequest();
        joinEventRequest.setUserId(userCreateRequest.getUserName());
        joinEventRequest.setEventId(eventResponse.getEventId());

        // When & Then
        queryUtility.userControllerClient.joinEvent(userCreateRequest.getUserName(), joinEventRequest)
                .andExpect(status().isOk());
    }

    @Test
    void joinEvent_WhenEventDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);
        JoinEventRequest joinEventRequest = new JoinEventRequest();
        joinEventRequest.setUserId(userCreateRequest.getUserName());
        joinEventRequest.setEventId(mockNeat.cities().us().get());


        // When & Then
        queryUtility.userControllerClient.joinEvent(userCreateRequest.getUserName(), joinEventRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void joinEvent_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        EventCreateRequest request = new EventCreateRequest();
        request.setEventSponsor("f");
        request.setLocation("f");
        request.setName("f");
        request.setEndTime(LocalDateTime.now().plusDays(3).toString());
        request.setStartTime(LocalDateTime.now().toString());
        ResultActions temp = queryUtility.eventControllerClient.addNewEvent(request);
        EventResponse eventResponse = objectMapper.readValue(temp.andReturn().getResponse().getContentAsString(), EventResponse.class);
        JoinEventRequest joinEventRequest = new JoinEventRequest();
        joinEventRequest.setUserId(mockNeat.users().get());
        joinEventRequest.setEventId(eventResponse.getEventId());


        // When & Then
        queryUtility.userControllerClient.joinEvent(mockNeat.users().get(), joinEventRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        // GIVEN


        // WHEN


        // THEN
        queryUtility.userControllerClient.getAllUsers()
                .andExpect(status().isOk());

    }



    @Test
    void shareEventsWithFriend_ShouldReturnOk() throws Exception {
        // GIVEN
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);

        EventCreateRequest request = new EventCreateRequest();
        request.setEventSponsor("f");
        request.setLocation("f");
        request.setName("f");
        request.setEndTime(LocalDateTime.now().plusDays(3).toString());
        request.setStartTime(LocalDateTime.now().toString());
        ResultActions temp = queryUtility.eventControllerClient.addNewEvent(request);
        EventResponse eventResponse = objectMapper.readValue(temp.andReturn().getResponse().getContentAsString(), EventResponse.class);

        // WHEN

        // THEN
        queryUtility.userControllerClient.shareEventsWithFriend(userCreateRequest.getUserName(), eventResponse.getEventId())
            .andExpect(status().isOk());
    }

    @Test
    void shareEventsWithFriend_FriendDoesNotExist_ShouldReturnNotFound() throws Exception {
        // GIVEN

        EventCreateRequest request = new EventCreateRequest();
        request.setEventSponsor("f");
        request.setLocation("f");
        request.setName("f");
        request.setEndTime(LocalDateTime.now().plusDays(3).toString());
        request.setStartTime(LocalDateTime.now().toString());
        ResultActions temp = queryUtility.eventControllerClient.addNewEvent(request);
        EventResponse eventResponse = objectMapper.readValue(temp.andReturn().getResponse().getContentAsString(), EventResponse.class);

        // WHEN

        // THEN
        queryUtility.userControllerClient.shareEventsWithFriend(mockNeat.users().get(), eventResponse.getEventId())
                .andExpect(status().isNotFound());
    }



    @Test
    void getEventsAttendedByFriends_ShouldReturnEvents() throws Exception {
        // GIVEN
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);

        // WHEN


        // THEN
        queryUtility.userControllerClient.getEventsAttendedByFriends(userCreateRequest.getUserName())
                .andExpect(status().isOk());
    }

    @Test
    void getEventsAttendedByFriends_FriendNotFound_ShouldReturnBadRequest() throws Exception {
        // GIVEN

        // WHEN


        // THEN
        queryUtility.userControllerClient.getEventsAttendedByFriends(mockNeat.users().get())
                .andExpect(status().isBadRequest());
    }


    @Test
    void addFriend_ShouldAddFriendSuccessfully() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);

        UserCreateRequest userCreateRequest2 = new UserCreateRequest();
        userCreateRequest2.setPassword("f");
        userCreateRequest2.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest2);
        AddFriendRequest addFriendRequest = new AddFriendRequest();
        addFriendRequest.setUserId(userCreateRequest.getUserName());
        addFriendRequest.setFriendId(userCreateRequest2.getUserName());

        // When & Then
        queryUtility.userControllerClient.addFriend(userCreateRequest.getUserName(), addFriendRequest)
                .andExpect(status().isOk());
    }
    @Test
    void addFriend_WhenFriendNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);
        AddFriendRequest addFriendRequest = new AddFriendRequest();
        addFriendRequest.setUserId(userCreateRequest.getUserName());
        addFriendRequest.setFriendId(mockNeat.users().get());

        // When & Then
        queryUtility.userControllerClient.addFriend(userCreateRequest.getUserName(), addFriendRequest)
                .andExpect(status().isNotFound());
    }


    @Test
    void addFriend_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPassword("f");
        userCreateRequest.setUserName(mockNeat.users().get());
        queryUtility.userControllerClient.addUser(userCreateRequest);
        AddFriendRequest addFriendRequest = new AddFriendRequest();
        addFriendRequest.setUserId(mockNeat.users().get());
        addFriendRequest.setFriendId(userCreateRequest.getUserName());

        // When & Then
        queryUtility.userControllerClient.addFriend(mockNeat.users().get(), addFriendRequest)
                .andExpect(status().isNotFound());
    }


    /*
    @Test
    void updateUser_ShouldUpdateUserSuccessfully() throws Exception {
        // Given
        UserUpdateRequest request = new UserUpdateRequest("userId", "userName", "password", "email@example.com", "First", "Last", "type", new ArrayList<>(), new ArrayList<>());
        UserRecord updatedUser = new UserRecord();
        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/User")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }


     */


}
