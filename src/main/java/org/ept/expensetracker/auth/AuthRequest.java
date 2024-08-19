package org.ept.expensetracker.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class AuthRequest {

    private String email;
    private String password;

}
