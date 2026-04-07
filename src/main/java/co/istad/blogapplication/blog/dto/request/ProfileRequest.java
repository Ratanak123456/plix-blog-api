package co.istad.blogapplication.blog.dto.request;

import lombok.Data;

@Data
public class ProfileRequest {
    private String fullName;
    private String bio;
    private String profileImage;
}
