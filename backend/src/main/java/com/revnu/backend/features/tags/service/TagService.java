package com.revnu.backend.features.tags.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.tags.dto.TagDto;
import com.revnu.backend.features.tags.model.Tag;
import com.revnu.backend.features.tags.repository.TagRepository;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public TagService(TagRepository tagRepository, UserRepository userRepository,
            RestaurantRepository restaurantRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional(readOnly = true)
    public List<TagDto> getAllTags(String email) {
        Restaurant restaurant = getRestaurant(email);
        return tagRepository.findByRestaurant(restaurant).stream()
                .map(tag -> new TagDto(tag.getId(), tag.getName(), tag.getType()))
                .collect(Collectors.toList());
    }

    @Transactional
    public TagDto addTag(String email, TagDto request) {
        Restaurant restaurant = getRestaurant(email);
        String cleanName = request.name().trim().toUpperCase();

        Tag tag = tagRepository.findByNameAndRestaurantIdAndType(cleanName, restaurant.getId(), request.type())
                .orElseGet(() -> {
                    Tag newTag = Tag.builder()
                            .name(cleanName)
                            .type(request.type())
                            .restaurant(restaurant)
                            .build();
                    return tagRepository.save(newTag);
                });

        return new TagDto(tag.getId(), tag.getName(), tag.getType());
    }

    @Transactional
    public void deleteTag(String email, UUID tagId) {
        Restaurant restaurant = getRestaurant(email);
        Tag tag = getValidatedTag(tagId, restaurant);
        tagRepository.delete(tag);
    }

    private Restaurant getRestaurant(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return restaurantRepository.findByOwner(user)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
    }

    private Tag getValidatedTag(UUID tagId, Restaurant restaurant) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
        if (!tag.getRestaurant().getId().equals(restaurant.getId())) {
            throw new SecurityException("Unauthorized access to this tag.");
        }
        return tag;
    }

}
