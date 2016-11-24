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
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleRule;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ConcurrentModificationException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserResourceTest {

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @Inject
    private UserService userService;

    @Inject
    private UriInfo info;

    @ObjectUnderTest(postConstruct = true)
    private UserResource userResource;

    @Test
    public void readExistingUser() {
        // arrange
        final User testUser = User.builder()
                .userId(1L)
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();
        when(userService.read(1L)).thenReturn(Optional.of(testUser));

        // act
        final Response response = userResource.read(1L);

        // assert
        assertThat(response.getStatus(), is(200));
        assertThat(response.getEntity(), is(testUser));
    }

    @Test(expected = NotFoundException.class)
    public void readNonExistingUser() {
        // arrange
        when(userService.read(1L)).thenReturn(Optional.empty());

        // act
        userResource.read(1L);

        // assert
    }

    @Test
    public void updateExistingUser() throws URISyntaxException {
        // arrange
        final User testUser = User.builder()
                .userId(1L)
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();
        when(userService.read(1L)).thenReturn(Optional.of(testUser));
        final UriBuilder uriBuilder = mock(UriBuilder.class);
        when(uriBuilder.build()).thenReturn(new URI("/users/1"));
        when(info.getAbsolutePathBuilder()).thenReturn(uriBuilder);

        // act
        final Response response = userResource.update(1L, testUser, info);

        // assert
        assertThat(response.getHeaderString("Location"), endsWith("/users/1"));
    }

    @Test(expected = NotFoundException.class)
    public void updateNonExistingUser() {
        // arrange
        final User testUser = User.builder()
                .userId(1L)
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();
        when(userService.read(1L)).thenReturn(Optional.empty());

        // act
        userResource.update(1L, testUser, info);

        // assert
    }

    @Test(expected = ConcurrentModificationException.class)
    public void updateConflictingUser() throws URISyntaxException {
        // arrange
        final User testUser = User.builder()
                .userId(1L)
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();
        when(userService.read(1L)).thenReturn(Optional.of(testUser));
        when(userService.update(testUser)).thenThrow(ConcurrentModificationException.class);

        // act
        userResource.update(1L, testUser, info);

        // assert
    }

    @Test
    public void deleteExistingUser() {
        // arrange
        final User testUser = User.builder()
                .userId(1L)
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();
        when(userService.read(1L)).thenReturn(Optional.of(testUser));

        // act
        final Response response = userResource.delete(1L);

        // assert
        assertThat(response.getStatus(), is(204));
    }

    @Test(expected = NotFoundException.class)
    public void deleteNonExistingUser() {
        // arrange
        when(userService.read(1L)).thenReturn(Optional.empty());

        // act
        userResource.delete(1L);

        // assert
    }

}