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
package ch.sportchef.users.boundary;

import ch.sportchef.users.controller.UserService;
import ch.sportchef.users.entity.User;
import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleRule;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UsersResourceTest {

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @Inject
    private UserService userService;

    @Inject
    private UriInfo info;

    @ObjectUnderTest(postConstruct = true)
    private UsersResource usersResource;

    @Test
    public void createUser() throws URISyntaxException {
        // arrange
        final User testUser = User.builder()
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();
        when(userService.create(testUser)).thenReturn(testUser.toBuilder().userId(1L).version(1L).build());
        final UriBuilder uriBuilder = mock(UriBuilder.class);
        final URI uri = new URI("/users/1");
        when(uriBuilder.path((String) anyObject())).thenReturn(uriBuilder);
        when(uriBuilder.build()).thenReturn(uri);
        when(info.getAbsolutePathBuilder()).thenReturn(uriBuilder);

        // act
        final Response response = usersResource.create(testUser, info);

        // assert
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation(), is(uri));
    }

    @Test
    public void readAllUsers() {
        // arrange
        final User testUser = User.builder()
                .userId(1L)
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();
        when(userService.read()).thenReturn(ImmutableList.of(testUser));

        // act
        final Response response = usersResource.read();

        // assert
        final List<User> users = (List<User>) response.getEntity();
        assertThat(users.size(), is(1));
        assertThat(users, contains(testUser));
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void readEmptyUsers() {
        // arrange
        when(userService.read()).thenReturn(ImmutableList.of());

        // act
        final Response response = usersResource.read();

        // assert
        final List<User> users = (List<User>) response.getEntity();
        assertThat(users.isEmpty(), is(TRUE));
        assertThat(response.getStatus(), is(200));
    }

}