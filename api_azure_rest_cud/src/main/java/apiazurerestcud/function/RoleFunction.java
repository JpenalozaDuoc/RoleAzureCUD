package apiazurerestcud.function;

import java.sql.SQLException;
import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import apiazurerestcud.exception.RoleNotFoundException;
import apiazurerestcud.model.Role;
import apiazurerestcud.service.RoleService;

public class RoleFunction {

    private final RoleService roleService = new RoleService();

    // Crear un nuevo rol
    @FunctionName("createRole")
    public HttpResponseMessage createRole(
        @HttpTrigger(name = "req", methods = {HttpMethod.POST}, route = "roles") HttpRequestMessage<Optional<Role>> request,
        ExecutionContext context) {

        try {
            Role role = request.getBody().orElseThrow(() -> new IllegalArgumentException("Role data is required"));
            roleService.createRole(role);
            return request.createResponseBuilder(HttpStatus.CREATED)
                            .body("Role created successfully")
                            .build();
        } catch (IllegalArgumentException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"" + e.getMessage() + "\"}")
                            .build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\": \"Database error: " + e.getMessage() + "\"}")
                            .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"Error creating role: " + e.getMessage() + "\"}")
                            .build();
        }
    }

    // Actualizar un rol
    @FunctionName("updateRole")
    public HttpResponseMessage updateRole(
        @HttpTrigger(name = "req", methods = {HttpMethod.PUT}, route = "roles/{id}") HttpRequestMessage<Optional<Role>> request,
        @BindingName("id") String id, ExecutionContext context) {

        try {
            Role role = request.getBody().orElseThrow(() -> new IllegalArgumentException("Role data is required"));
            roleService.updateRole(Long.valueOf(id), role);
            return request.createResponseBuilder(HttpStatus.OK)
                            .body("Role updated successfully")
                            .build();
        } catch (RoleNotFoundException e) {  // Capturamos la excepción personalizada si no se encuentra el rol
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                            .body("{\"error\": \"" + e.getMessage() + "\"}")
                            .build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\": \"Database error: " + e.getMessage() + "\"}")
                            .build();
        } catch (IllegalArgumentException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"" + e.getMessage() + "\"}")
                            .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"Error updating role: " + e.getMessage() + "\"}")
                            .build();
        }
    }

    // Eliminar un rol
    @FunctionName("deleteRole")
    public HttpResponseMessage deleteRole(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, route = "roles/{id}") HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id, ExecutionContext context) {

        try {
        roleService.deleteRole(Long.valueOf(id));  // Este método puede lanzar RoleNotFoundException
        return request.createResponseBuilder(HttpStatus.OK)
                      .body("Role deleted successfully")
                      .build();
        } catch (RoleNotFoundException e) {  // Capturamos la excepción personalizada
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"" + e.getMessage() + "\"}")
                        .build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\": \"Database error: " + e.getMessage() + "\"}")
                        .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Error deleting role: " + e.getMessage() + "\"}")
                        .build();
        }
    }
}
