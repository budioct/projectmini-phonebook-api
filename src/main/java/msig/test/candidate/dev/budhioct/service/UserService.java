package msig.test.candidate.dev.budhioct.service;

import msig.test.candidate.dev.budhioct.dto.UserDTO;
import msig.test.candidate.dev.budhioct.model.Users;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface UserService {

    void register(UserDTO.RegisterUserRequest request);
    UserDTO.TokenResponse login(UserDTO.LoginUserRequest request);
    void logout(Users users);
    UserDTO.UserResponse update(Users users, UserDTO.UpdateUserRequest request);
    Page<UserDTO.UserResponse> getListUsers(Map<String, Object> filter);
    UserDTO.UserResponse getDetailUser(Users users);

}
