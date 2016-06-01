package com.matic.lugarenelbar;

import junit.framework.Assert;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by blueriver on 25/05/2016.
 */
public class LoginActivityTest {

    @Test
    public void loginActivity_isEmailValid_returnsTrue() {
        assertThat((new LoginActivity()).isEmailValid("admin@admin.com"), is(true));
    }

    @Test
    public void loginActivity_isEmailValid_returnsFalse() {
        assertThat((new LoginActivity()).isEmailValid("admin"), is(false));
    }

    @Test
    public void loginActivity_isPasswordValid_returnsTrue() {
        assertThat((new LoginActivity()).isPasswordValid("admin"), is(true));
    }

    @Test
    public void loginActivity_isPasswordValid_returnsFalse() {
        assertThat((new LoginActivity()).isEmailValid(""), is(false));
    }
}
