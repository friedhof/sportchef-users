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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.net.URI;
import java.util.List;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    private UserService userService;

    @Inject
    public UsersResource(@NonNull final UserService userService) {
        this.userService = userService;
    }

    @POST
    public Response create(@NonNull @Valid final User user,
                           @NonNull @Context final UriInfo info) {
        final User createdUser = userService.create(user);
        final long userId = createdUser.getUserId();
        final URI uri = info.getAbsolutePathBuilder().path(File.separator + userId).build();
        return Response.created(uri).build();
    }

    @GET
    public Response read() {
        final List<User> users = userService.read();
        return Response.ok(users).build();
    }

}
