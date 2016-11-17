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
import lombok.NonNull;
import pl.setblack.airomem.core.SimpleController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserService {

    private SimpleController<UserRepository> userController;

    @PostConstruct
    public void setupResources() {
        this.userController = PersistenceManager.createSimpleController(User.class, UserRepository::new);
    }

    @PreDestroy
    public void cleanupResources() {
        this.userController.close();
    }

    public User create(@NonNull final User user) {
        return this.userController.executeAndQuery(mgr -> mgr.create(user));
    }

    public List<User> read() {
        return this.userController.readOnly().read();
    }

    public Optional<User> read(@NonNull final Long userId) {
        return this.userController.readOnly().read(userId);
    }

    public User update(@NonNull final User user) {
        return this.userController.executeAndQuery(mgr -> mgr.update(user));
    }

    public void delete(@NonNull final Long userId) {
        this.userController.execute(mgr -> mgr.delete(userId));
    }

}
