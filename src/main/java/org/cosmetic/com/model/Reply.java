package org.cosmetic.com.model;


import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reply {
    private String userNameAdmin;
    private String content;
    private Instant repliedAt;
}
