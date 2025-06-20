package io.github.votandov5o.v5oauthenticationsupportservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationDTO {
    private String name;
    private String displayName;
    private String url;
}
