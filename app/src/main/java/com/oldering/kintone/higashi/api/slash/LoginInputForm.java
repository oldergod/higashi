package com.oldering.kintone.higashi.api.slash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
class LoginInputForm {
    private String username;
    private String password;
}
