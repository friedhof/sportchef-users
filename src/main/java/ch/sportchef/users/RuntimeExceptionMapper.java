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
package ch.sportchef.users;

import lombok.NonNull;
import lombok.Value;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ConcurrentModificationException;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(@NonNull final RuntimeException e) {
        return createResponse(handleException(e));
    }

    private ResponseData handleException(@NonNull final Throwable e) {
        if (e instanceof ConcurrentModificationException) {
            return new ResponseData(CONFLICT, e.getMessage());
        }
        if (e instanceof NotFoundException) {
            return new ResponseData(NOT_FOUND, e.getMessage());
        }
        if (e.getCause() != null) {
            return handleException(e.getCause());
        }
        return new ResponseData(INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private Response createResponse(@NonNull ResponseData responseData) {
        final JsonObject entity = Json.createObjectBuilder()
                .add("status", responseData.getStatus().getStatusCode())
                .add("message", responseData.getMessage())
                .build();
        return Response.status(responseData.getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(entity).build();
    }

    @Value
    private class ResponseData {
        private Response.Status status;
        private String message;
    }

}