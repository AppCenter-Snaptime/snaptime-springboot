package me.snaptime.crawling.service;

import me.snaptime.crawling.service.provider.PhotoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class CrawlingServiceImpl implements CrawlingService {
    private final Map<String, PhotoProvider> providers;

    @Autowired
    public CrawlingServiceImpl(List<PhotoProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(provider -> provider.getClass().getSimpleName().toLowerCase(), Function.identity()));
    }


    @Override
    public byte[] getImage(String providerName, String page_url) {
        PhotoProvider provider = providers.get(providerName.toLowerCase());
        String image_url = provider.crawlingImageURL(page_url);
        return provider.getImageBytes(image_url);
    }
}
