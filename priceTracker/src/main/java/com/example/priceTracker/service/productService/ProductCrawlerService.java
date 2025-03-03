package com.example.priceTracker.service.productService;

import com.example.priceTracker.config.PlatformConfig;
import com.example.priceTracker.domain.product.Product;
import com.example.priceTracker.domain.user.User;
import com.example.priceTracker.dto.productDto.ProductResponseDTO;
import com.example.priceTracker.repository.userRepository.UserRepository;
import com.example.priceTracker.service.userService.UserService;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductCrawlerService {

    private final List<PlatformConfig> platformConfigs = new ArrayList<>();
    private final int MAX_PRODUCTS = 10;

    public ProductCrawlerService() {
        platformConfigs.add(new PlatformConfig(
                "https://web.joongna.com/search/?keyword=",
                // 이미지 선택자는 사용하지 않음
                "a.relative.group.box-border", // 상품 링크 선택자
                "div.relative.w-full.rounded-md", // 상품 컨테이너
                "중고나라"
        ));
    //    platformConfigs.add(new PlatformConfig("https://m.bunjang.co.kr/search/", ".item-title", ".item-price", ".item-description", ".item-link", "Bunjang"));
    }

    public List<ProductResponseDTO.ProductDto> crawlProduct(String searchKeyword) throws IOException {
        List<ProductResponseDTO.ProductDto> products = new ArrayList<>();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--lang=ko");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            for (PlatformConfig platformConfig : platformConfigs) {

                int productCount = 0;

                String searchUrl = platformConfig.getBaseUrl() + searchKeyword;
                log.info("다음 URL에서 상품 크롤링 중: {}", searchUrl);
                driver.get(searchUrl);

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul.grid.search-results")));

                Thread.sleep(2000);

                String pageSource = driver.getPageSource();
                Document doc = Jsoup.parse(pageSource);

                Elements productElements = doc.select("ul.grid.search-results > li");
                log.info("{}개의 상품 요소를 찾았습니다", productElements.size());

                for (Element productElement : productElements) {
                    if (productCount >= MAX_PRODUCTS) {
                        break;
                    }

                    try {
                        Element linkElement = productElement.selectFirst("a.relative.group.box-border");

                        if (linkElement == null) {
                            continue;
                        }

                        String url = linkElement.attr("href");
                        if (!url.startsWith("http")) {
                            url = "https://web.joongna.com" + url;
                        }

                        // 상품 세부 정보 추출
                        String title = productElement.select("img").attr("alt");
                        String priceText = productElement.select("div.font-semibold").text();
                        String description = productElement.select("span.text-sm.text-gray-400").text();
                   //     String imageUrl = productElement.select("img").attr("src");

                        // 가격 정리 및 파싱
                        long priceLong = parsePrice(priceText);

                        ProductResponseDTO.ProductDto product = ProductResponseDTO.ProductDto.builder()
                                .title(title)
                                .price(priceLong)
                                .description(description)
                                .url(url)
                            //    .imageUrl(imageUrl)
                                .platform(platformConfig.getPlatformName())
                                .build();

                        products.add(product);
                        productCount++;
                        log.debug("상품 추가됨: {}", title);
                    } catch (Exception e) {
                        log.error("상품 요소 처리 중 오류 발생: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("크롤링 중 오류 발생: ", e);
        } finally {
            driver.quit();
        }

        return products;
    }

    private long parsePrice(String priceText) {
        // 가격 문자열에서 숫자, 쉼표 및 '원'을 제거
        String cleanedPrice = priceText.replaceAll("[^0-9]", "");

        if (cleanedPrice.isEmpty()) {
            log.warn("가격 문자열이 비어있습니다: {}", priceText);
            return 0;
        }

        try {
            return Long.parseLong(cleanedPrice);
        } catch (NumberFormatException e) {
            log.warn("가격 파싱 실패: {}", priceText);
            return 0;
        }
    }

}