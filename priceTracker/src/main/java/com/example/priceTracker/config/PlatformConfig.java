package com.example.priceTracker.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlatformConfig {

    private String baseUrl;           // 검색 기본 URL
    // private String imageSelector;  // 이미지 선택자 (제거됨)
    private String urlSelector;       // 상품 링크 선택자
    private String containerSelector; // 상품 컨테이너 선택자
    private String platformName;      // 플랫폼 이름

    // 필요한 경우 추가 선택자 메소드
    public String getTitleSelector() {
        // 플랫폼에 따라 다른 선택자 반환
        if ("중고나라".equals(platformName)) {
            return "img[alt]";  // alt 속성을 제목으로 사용
        }
        return "h2.product-title";  // 기본값
    }

    public String getPriceSelector() {
        if ("중고나라".equals(platformName)) {
            return "span.font-semibold";
        }
        return ".price";  // 기본값
    }

    public String getDescriptionSelector() {
        if ("중고나라".equals(platformName)) {
            return "span.text-sm.text-gray-400";
        }
        return ".description";  // 기본값
    }

}

