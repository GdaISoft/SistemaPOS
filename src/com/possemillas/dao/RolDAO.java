package com.possemillas.dao;

import com.possemillas.models.Rol;
import java.sql.SQLException;
import java.util.List;

public interface RolDAO {
    List<Rol> listarActivos() throws SQLException;
}
