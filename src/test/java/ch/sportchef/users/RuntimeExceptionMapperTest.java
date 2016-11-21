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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class RuntimeExceptionMapperTest {

    private RuntimeException exception;
    private String message;
    private Response.Status status;

    @Parameterized.Parameters(name = "{2}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                { new ConcurrentModificationException("Foobar"), "Foobar", CONFLICT },
                { new NotFoundException("Foobar"),               "Foobar", NOT_FOUND },
                { new RuntimeException("Foobar"),                "Foobar", INTERNAL_SERVER_ERROR }
        });
    }

    public RuntimeExceptionMapperTest(@NonNull final RuntimeException exception,
                                      @NonNull final String message,
                                      @NonNull final Response.Status status) {
        this.exception = exception;
        this.message = message;
        this.status = status;
    }

    @Test
    public void toResponse() {
        // arrange
        final JsonObject expectedEntity = Json.createObjectBuilder()
                .add("status", this.status.getStatusCode())
                .add("message", this.message)
                .build();
        final RuntimeExceptionMapper runtimeExceptionMapper = new RuntimeExceptionMapper();

        // act
        final Response response = runtimeExceptionMapper.toResponse(this.exception);

        // assert
        assertThat(response.getStatus(), is(this.status.getStatusCode()));
        assertThat(response.getEntity(), equalTo(expectedEntity));
    }

}