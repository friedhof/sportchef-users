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

import ch.sportchef.users.entity.User;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import fish.payara.micro.BootstrapException;
import fish.payara.micro.PayaraMicro;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class UsersResourceIT {

    @BeforeClass
    public static void setUp() throws BootstrapException {
        PayaraMicro.getInstance()
                .addDeployment("./build/libs/sportchef-users.war")
                .bootStrap();
    }

    @AfterClass
    public static void tearDown() throws BootstrapException {
        PayaraMicro.getInstance().shutdown();
    }

    @Test
    public void crud() {
        final User user = User.builder()
                .firstName("Test Firstname")
                .lastName("Test Lastname")
                .phone("Test Phone")
                .email("test@email.test")
                .build();

        final Long userId = testCreateUser(user);
        final User readUser = testReadUser(user.toBuilder().userId(userId).build());
        testUpdate(readUser);
        testDelete(userId);
    }

    private Long testCreateUser(final User user) {
        // create a new user with success
        final Response response = given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(user))
                .post("/users")
            .then()
                .assertThat()
                .statusCode(201)
                    .header("Location", notNullValue())
                    .extract().response();
        final String[] locationParts = response.header("Location").split("/");
        final Long userId = Long.parseLong(locationParts[locationParts.length - 1]);

        // create a new user should fail with a bad request
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(user.toBuilder().lastName(null).build()))
                .post("/users")
            .then()
                .assertThat()
                    .statusCode(400);

        return userId;
    }

    private User testReadUser(final User user) {
        // read all users should contain the new user
        final String allUsersJson = given()
            .when()
                .get("/users")
            .then()
                .assertThat()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .extract().response().asString();
        final List<User> allUsers = Lists.newArrayList(new Gson().fromJson(allUsersJson, User[].class));
        assertThat(allUsers.contains(user), is(true));

        // read the new user should finish successful
        final String oneUserJson = given()
            .when()
                .get("/users/" + user.getUserId())
            .then()
                .assertThat()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .extract().response().asString();
        final User oneUser = new Gson().fromJson(oneUserJson, User.class);
        assertThat(oneUser, is(user));

        // read a non-existing user should return a not found
        given()
            .when()
                .get("/users/" + Long.MAX_VALUE)
            .then()
                .assertThat()
                    .statusCode(404);

        return oneUser;
    }

    private void testUpdate(final User user) {
        // update the new user with success
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(user.toBuilder().phone("Phone Updated").build()))
                .put("/users/" + user.getUserId())
            .then()
                .assertThat()
                    .statusCode(200)
                    .header("Location", endsWith("/users/" + user.getUserId()));

        // update the new user should fail with a conflict
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(user.toBuilder().phone("Phone Conflict").build()))
                .put("/users/" + user.getUserId())
            .then()
                .assertThat()
                    .statusCode(409);

        // update the new user should fail with a bad request
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(user.toBuilder().phone(null).build()))
                .put("/users/" + user.getUserId())
            .then()
                .assertThat()
                    .statusCode(400);

        // update a non-existing user should fail with a not found
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(new Gson().toJson(user))
                .put("/users/" + Long.MAX_VALUE)
            .then()
                .assertThat()
                    .statusCode(404);
    }

    private void testDelete(final Long userId) {
        // delete the new user with success
        given()
            .when()
                .delete("/users/" + userId)
            .then()
                .assertThat()
                    .statusCode(204);

        // delete a non-existing user should fail with a not found
        given()
            .when()
                .delete("/users/" + userId)
            .then()
                .assertThat()
                    .statusCode(404);
    }

}
