package com.example.project_economic.service;

import com.example.project_economic.dto.request.PermissionRequest;
import com.example.project_economic.dto.response.PermissionResponse;
import java.util.List;

public interface PermissionService {
  // Fetch
  List<PermissionResponse> getAll();

  // Add/Update/Delete
  PermissionResponse add(PermissionRequest permissionRequest);

  PermissionResponse update(PermissionRequest permissionRequest);

  Long delete(Long id);
}
