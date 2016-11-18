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
package ch.sportchef.users.controller;


import ch.sportchef.users.entity.User;
import lombok.NonNull;
import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class UserRepositoryTest {

    private User createUser(@NonNull final UserRepository userRepository) {
        final User baseUser = User.builder()
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();

        return userRepository.create(baseUser);
    }

    @Test
    public void readByUserIdFound() {
        // arrange
        final UserRepository userRepository = new UserRepository();
        final User user = createUser(userRepository);

        // act
        final Optional<User> userOptional = userRepository.read(user.getUserId());

        // assert
        assertThat(userOptional.orElseGet(null), is(user));
    }

    @Test
    public void readByUserIdNotFound() {
        // arrange
        final UserRepository userRepository = new UserRepository();

        // act
        final Optional<User> userOptional = userRepository.read(1L);

        // assert
        assertThat(userOptional.isPresent(), is(false));
    }

    @Test
    public void readAllFound() {
        // arrange
        final UserRepository userRepository = new UserRepository();
        final User user1 = createUser(userRepository);
        final User user2 = createUser(userRepository);

        // act
        final List<User> userList = userRepository.read();

        // assert
        assertThat(userList, notNullValue());
        assertThat(userList.size(), is(2));
        assertThat(userList, contains(user1, user2));
    }

    @Test
    public void readAllNotFound() {
        // arrange
        final UserRepository userRepository = new UserRepository();

        // act
        final List<User> userList = userRepository.read();

        // assert
        assertThat(userList, notNullValue());
        assertThat(userList.size(), is(0));
    }

    @Test
    public void updateSuccess() {
        // arrange
        final UserRepository userRepository = new UserRepository();
        final User createdUser = createUser(userRepository);
        final User userToUpdate = createdUser.toBuilder()
                .phone("Changed Phone")
                .build();

        // act
        final User updatedUser =  userRepository.update(userToUpdate);

        // assert
        assertThat(updatedUser, is(equalTo(userToUpdate)));
        assertThat(updatedUser.getVersion(), is(not(equalTo(userToUpdate.getVersion()))));
    }

    @Test(expected = ConcurrentModificationException.class)
    public void updateWithConflict() {
        // arrange
        final UserRepository userRepository = new UserRepository();
        final User createdUser = createUser(userRepository);
        final User userToUpdate1 = createdUser.toBuilder()
                .phone("Changed Phone 1")
                .build();
        final User userToUpdate2 = createdUser.toBuilder()
                .phone("Changed Phone 2")
                .build();

        // act
        userRepository.update(userToUpdate1);
        userRepository.update(userToUpdate2);

        // assert
    }

    @Test
    public void deleteExistingUser() {
        // arrange
        final UserRepository userRepository = new UserRepository();
        final User user = createUser(userRepository);

        // act
        userRepository.delete(user.getUserId());

        // assert
        assertThat(userRepository.read(user.getUserId()), is(Optional.empty()));
    }

    @Test
    public void deleteNonExistingUser() {
        // arrange
        final UserRepository userRepository = new UserRepository();

        // act
        userRepository.delete(1L);

        // assert
        assertThat(userRepository.read(1L), is(Optional.empty()));
    }

}