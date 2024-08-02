package msig.test.candidate.dev.budhioct.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import msig.test.candidate.dev.budhioct.common.Models;
import msig.test.candidate.dev.budhioct.common.ValidationService;
import msig.test.candidate.dev.budhioct.dto.UserDTO;
import msig.test.candidate.dev.budhioct.model.Users;
import msig.test.candidate.dev.budhioct.repository.UserRepository;
import msig.test.candidate.dev.budhioct.service.UserService;
import msig.test.candidate.dev.budhioct.utilities.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ValidationService validation;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void register(UserDTO.RegisterUserRequest request) {
        validation.validate(request);

        if (userRepository.findFirstByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already in use");
        }

        Users user = new Users();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);
    }

    @Transactional
    public UserDTO.TokenResponse login(UserDTO.LoginUserRequest request) {
        validation.validate(request);

        Users users = userRepository.findFirstByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong"));

        if (BCrypt.checkpw(request.getPassword(), users.getPassword())) {
            users.setToken(UUID.randomUUID().toString());
            users.setTokenExpiredAt(next2Days());
            userRepository.save(users);

            return UserDTO.tokenResponse(users);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
        }
    }

    private long next2Days() {
        return System.currentTimeMillis() + (1000 * 16 * 24 * 2);
    }

    @Transactional
    public void logout(Users users) {

        users.setToken(null);
        users.setTokenExpiredAt(null);
        userRepository.save(users);
    }


    @Transactional
    public UserDTO.UserResponse update(Users users, UserDTO.UpdateUserRequest request) {
        validation.validate(request);

        if (Objects.nonNull(request.getName())) {
            users.setName(request.getName());
        }
        if (Objects.nonNull(request.getPassword())) {
            users.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(users);
        return UserDTO.toUserResponse(users);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO.UserResponse> getListUsers(Map<String, Object> filter) {
        Models<Users> models = new Models<>();
        Page<Users> usersPage = userRepository.findAll(models.where(filter), models.pageableSort(filter));
        List<UserDTO.UserResponse> usersResponse = usersPage.getContent().stream().map(UserDTO::toUserResponse).collect(Collectors.toList());

        if (usersResponse.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "List Users not found");
        }

        return new PageImpl<>(usersResponse, usersPage.getPageable(), usersPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public UserDTO.UserResponse getDetailUser(Users users) {
        return UserDTO.toUserResponse(users);
    }

}
