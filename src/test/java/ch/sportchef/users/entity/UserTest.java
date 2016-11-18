/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.users.entity;

import lombok.val;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserTest {

    private static final Long USER_ID = 1L;
    private static final String USER_FIRSTNAME = "Test Firstname";
    private static final String USER_LASTNAME = "Test Lastname";
    private static final String USER_PHONE = "Test Phone";
    private static final String USER_EMAIL = "test@email.test";

    private static User user;

    @BeforeClass
    public static void setUp() {
        user = User.builder()
                .userId(USER_ID)
                .firstName(USER_FIRSTNAME)
                .lastName(USER_LASTNAME)
                .phone(USER_PHONE)
                .email(USER_EMAIL)
                .build();
    }

    @AfterClass
    public static void tearDown() {
        user = null;
    }

    @Test
    public void getUserId() {
        assertThat(user.getUserId(), is(USER_ID));
    }

    @Test
    public void getFirstName() {
        assertThat(user.getFirstName(), is(USER_FIRSTNAME));
    }

    @Test
    public void getLastName() {
        assertThat(user.getLastName(), is(USER_LASTNAME));
    }

    @Test
    public void getPhone() {
        assertThat(user.getPhone(), is(USER_PHONE));
    }

    @Test
    public void getEmail() {
        assertThat(user.getEmail(), is(USER_EMAIL));
    }

    @Test
    public void toStringTest() {
        // arrange
        val toStringExpect = String.format(
                "User(userId=%d, firstName=%s, lastName=%s, phone=%s, email=%s, version=%d)",
                USER_ID, USER_FIRSTNAME, USER_LASTNAME, USER_PHONE, USER_EMAIL, user.getVersion());

        // act
        val toString = user.toString();

        // assert
        assertThat(toString, is(toStringExpect));
    }

}