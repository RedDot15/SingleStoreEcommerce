package com.example.project_economic.service;

import com.example.project_economic.dto.request.RoleRequest;
import com.example.project_economic.dto.response.RoleResponse;
import java.util.List;

public interface RoleService {
  // Fetch
  List<RoleResponse> getAll();

  // Add/Update/Delete
  RoleResponse add(RoleRequest roleRequest);

  RoleResponse update(RoleRequest roleRequest);

  Long delete(Long id);
}
