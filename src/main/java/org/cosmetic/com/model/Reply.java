package org.cosmetic.com.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reply {
    private String userNameAdmin;
    private String content;
    private Instant repliedAt;
}
