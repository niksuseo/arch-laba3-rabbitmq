package com.misis.archapp.user.controller;

import com.misis.archapp.user.dto.UserCreateDTO;
import com.misis.archapp.user.dto.UserDTO;
import com.misis.archapp.user.dto.UserUpdateDTO;
import com.misis.archapp.user.service.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserRestApiController {

    private final UserService userService;
    private final Counter userRequestsCounter;
    private final MeterRegistry meterRegistry;

    @Autowired
    public UserRestApiController(
        UserService userService,
        MeterRegistry meterRegistry
    ) {
        this.userService = userService;
	this.meterRegistry = meterRegistry;

        this.userRequestsCounter = Counter.builder("user_requests_total")
            .description("Total number of requests to the user API")
            .register(meterRegistry);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
	userRequestsCounter.increment();
	meterRegistry.counter(
            "user_api_operations",
            "operation", "get_all"
        ).increment();
         return Timer.builder("user_get_all_duration")
            .description("Duration of getting all users")
            .register(meterRegistry)
            .record(() -> userService.getAllUsers());
    }

    @GetMapping("{id}")
    public UserDTO getUserById(@PathVariable("id") UUID id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDTO createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
	meterRegistry.counter(
            "user_api_operations",
            "operation", "create"
        ).increment();
        return userService.createUser(userCreateDTO);
    }

    @PatchMapping("{id}")
    public UserDTO updateUser(@PathVariable("id") UUID id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        return userService.updateUser(id, userUpdateDTO);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") UUID id) {

	meterRegistry.counter(
            "user_api_operations",
            "operation", "delete"
        ).increment();
        userService.deleteUser(id);
    }

}
