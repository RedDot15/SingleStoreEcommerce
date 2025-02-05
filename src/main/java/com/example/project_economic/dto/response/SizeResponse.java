package com.example.project_economic.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SizeResponse implements Comparable {
	Long id;

	String name;

	@Override
	public int compareTo(Object o) {
		return id.compareTo(((SizeResponse) o).getId());
	}
}
