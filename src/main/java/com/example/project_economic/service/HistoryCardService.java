package com.example.project_economic.service;

import com.example.project_economic.dto.request.HistoryCartDto;

import java.util.List;

public interface HistoryCardService {
//    void addProductToHistoryCard(Long userId, Long discount);

    List<HistoryCartDto> findByUserId(Long userId);
}
