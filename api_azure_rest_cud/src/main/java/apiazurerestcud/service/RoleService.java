package apiazurerestcud.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import apiazurerestcud.connection.DatabaseConnection;
import apiazurerestcud.exception.RoleNotFoundException;
import apiazurerestcud.model.Role;

public class RoleService {

    // Crear un nuevo rol
    public void createRole(Role role) throws SQLException {
        if (role.getName() == null || role.getName().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }

        String sql = "INSERT INTO roles (role_name) VALUES (?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, role.getName());
            stmt.executeUpdate();
        }
    }

    // Actualizar un rol
    public void updateRole(Long id, Role role) throws SQLException, RoleNotFoundException {
        if (role.getName() == null || role.getName().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }

        String sql = "UPDATE roles SET role_name = ? WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, role.getName());
            stmt.setLong(2, id);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RoleNotFoundException("Role not found with ID " + id);
            }
        }
    }

    // Eliminar un rol
    public void deleteRole(Long id) throws SQLException, RoleNotFoundException {
        String sql = "DELETE FROM roles WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int rowsDeleted = stmt.executeUpdate();
            
            if (rowsDeleted == 0) {
                throw new RoleNotFoundException("Role not found with ID " + id);
            }
        }
    }
}
