package com.example.holidayhome.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.holidayhome.entities.User;
import com.example.holidayhome.exceptions.NotFoundException;
import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.users.NewUserDTO;
import com.example.holidayhome.payloads.users.NewUserResponseDTO;
import com.example.holidayhome.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private Cloudinary cloudinaryUploader;
    @Autowired
    private PasswordEncoder bcrypt;

    public List<User> findAll() {
        return this.usersRepository.findAll();
    }

    public NewUserResponseDTO saveUser(NewUserDTO body) {
        if (usersRepository.existsByEmail(body.email())) throw new ValidationException("Email gia' in uso");

        User newUser = new User(body.firstName(), body.lastName(), body.email(), bcrypt.encode(body.password()), body.role());
        newUser.setPhone(body.phone());

        User saved = this.usersRepository.save(newUser);
        return new NewUserResponseDTO(saved.getId());
    }

    public User findById(UUID userId) {
        return this.usersRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
    }

    public User findByEmail(String email) {
        return this.usersRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Nessun utente con email " + email));
    }

    public User findByIdAndUpdate(UUID userId, NewUserDTO body) {
        User found = this.findById(userId);
        found.setFirstName(body.firstName());
        found.setLastName(body.lastName());
        found.setEmail(body.email());
        found.setPhone(body.phone());
        if (body.password() != null && !body.password().isBlank()) {
            found.setPassword(bcrypt.encode(body.password()));
        }
        found.setRole(body.role());
        return this.usersRepository.save(found);
    }

    public void findByIdAndDelete(UUID userId) {
        User found = this.findById(userId);
        this.usersRepository.delete(found);
    }

    public User uploadAvatar(UUID userId, MultipartFile file) throws IOException {
        User found = this.findById(userId);
        Map<?, ?> response = this.cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        found.setAvatarURL(response.get("secure_url").toString());
        return this.usersRepository.save(found);
    }
}
