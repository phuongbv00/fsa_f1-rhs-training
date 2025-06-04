package fsa.f1rhstraining.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private String fullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}