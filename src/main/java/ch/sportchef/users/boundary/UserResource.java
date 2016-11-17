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
import lombok.NonNull;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

@Path("users/{userId}")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserService userService;

    @Inject
    public UserResource(@NonNull final UserService userService) {
        this.userService = userService;
    }

    @GET
    public Response read(@NonNull @PathParam("userId") final Long userId) {
        final Optional<User> user = userService.read(userId);
        if (user.isPresent()) {
            return Response.ok(user.get()).build();
        }
        throw new NotFoundException(String.format("User with id '%d' not found.", userId));
    }

    @PUT
    public Response update(@NonNull @PathParam("userId") final Long userId,
                           @NonNull @Valid final User user,
                           @NonNull @Context final UriInfo info) {
        read(userId); // only update existing users
        final User userToUpdate = user.toBuilder()
                .userId(userId)
                .build();
        final User updatedUser = userService.update(userToUpdate);
        final URI uri = info.getAbsolutePathBuilder().build();
        return Response.ok(updatedUser).header("Location", uri.toString()).build();
    }

    @DELETE
    public Response delete(@NonNull @PathParam("userId") final Long userId) {
        read(userId); // only delete existing users
        userService.delete(userId);
        return Response.noContent().build();
    }

}
