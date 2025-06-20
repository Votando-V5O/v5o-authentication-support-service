package io.github.votandov5o.v5oauthenticationsupportservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String fiscalCode;
}
