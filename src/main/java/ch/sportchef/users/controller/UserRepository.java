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
import com.google.common.collect.ImmutableList;
import lombok.NonNull;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

class UserRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Long, User> allUsers = new ConcurrentHashMap<>();
    private final AtomicLong userSequence = new AtomicLong(0);

    User create(@NonNull final User user) {
        final Long userId = userSequence.incrementAndGet();
        final long version = user.hashCode();
        final User userToCreate = user.toBuilder().userId(userId).version(version).build();
        allUsers.put(userId, userToCreate);
        return userToCreate;
    }

    List<User> read() {
        return ImmutableList.copyOf(
                allUsers.values().stream()
                        .sorted(comparing(User::getLastName)
                                .thenComparing(User::getFirstName))
                        .collect(toList()));
    }

    Optional<User> read(@NonNull final Long userId) {
        return Optional.ofNullable(allUsers.get(userId));
    }

    User update(@NonNull final User user) {
        final Long userId = user.getUserId();
        final User previousUser = read(userId).orElse(null);
        if (previousUser == null) {
            return null; // non-existing users can't be updated
        }
        if (!previousUser.getVersion().equals(user.getVersion())) {
            throw new ConcurrentModificationException("You tried to update an user that was modified concurrently!");
        }
        final long version = user.hashCode();
        final User userToUpdate = user.toBuilder().version(version).build();
        allUsers.put(userId, userToUpdate);
        return userToUpdate;
    }

    User delete(@NonNull final Long userId) {
        return allUsers.remove(userId);
    }

}
