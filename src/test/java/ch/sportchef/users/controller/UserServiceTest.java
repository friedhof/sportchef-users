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

import ch.sportchef.users.PersistenceManager;
import ch.sportchef.users.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pl.setblack.airomem.core.SimpleController;
import pl.setblack.airomem.core.VoidCommand;

import java.io.Serializable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PersistenceManager.class)
public class UserServiceTest {

    @Test
    public void create() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();
        userService.setupResources();
        final User testUser = User.builder()
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();

        // act
        userService.create(testUser);

        // assert
        verify(simpleControllerMock, times(1)).executeAndQuery(anyObject());
    }

    @Test
    public void readAll() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        final UserRepository userRepositoryMock = mock(UserRepository.class);
        when(simpleControllerMock.readOnly()).thenReturn(userRepositoryMock);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();
        userService.setupResources();

        // act
        userService.read();

        // assert
        verify(simpleControllerMock, times(1)).readOnly();
        verify(userRepositoryMock, times(1)).read();
    }

    @Test
    public void readOne() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        final UserRepository userRepositoryMock = mock(UserRepository.class);
        when(simpleControllerMock.readOnly()).thenReturn(userRepositoryMock);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();
        userService.setupResources();

        // act
        userService.read(1L);

        // assert
        verify(simpleControllerMock, times(1)).readOnly();
        verify(userRepositoryMock, times(1)).read(1L);
    }

    @Test
    public void update() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();
        userService.setupResources();
        final User testUser = User.builder()
                .userId(1L)
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();

        // act
        userService.update(testUser);

        // assert
        verify(simpleControllerMock, times(1)).executeAndQuery(anyObject());
    }

    @Test
    public void delete() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final UserService userService = new UserService();
        userService.setupResources();

        // act
        userService.delete(1L);

        // assert
        verify(simpleControllerMock, times(1)).execute(any(VoidCommand.class));
    }

}