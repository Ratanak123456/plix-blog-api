package co.istad.blogapplication.blog.config;

import co.istad.blogapplication.blog.dto.response.UserResponse;
import co.istad.blogapplication.blog.entity.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.typeMap(User.class, UserResponse.class).addMappings(configurer -> {
            configurer.map(User::getProfileImage, UserResponse::setProfileImage);
            configurer.map(User::getCoverImage, UserResponse::setCoverImage);
            configurer.using(context -> context.getSource() == null ? null : context.getSource().toString())
                    .map(User::getRole, UserResponse::setRole);
            configurer.skip(UserResponse::setVerified);
        });
        return mapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
