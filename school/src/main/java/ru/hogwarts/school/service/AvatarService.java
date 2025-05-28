package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;

@Service
public class AvatarService {
    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);
    private static final String NOT_FOUND_MESSAGE = "Avatar with ID {} not found";

    private final AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    public Avatar createAvatar(Avatar avatar) {
        logger.info("Was invoked method for create avatar");
        logger.debug("Creating avatar for student with ID: {}", avatar.getStudent().getId());
        avatar.setId(null); // Сбрасываем ID для создания новой записи
        return avatarRepository.save(avatar);
    }

    public Avatar getAvatar(Long id) {
        logger.info("Was invoked method for get avatar with ID: {}", id);
        Avatar avatar = avatarRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND_MESSAGE, id);
                    return new IllegalArgumentException("Аватар с ID " + id + " не найден");
                });
        logger.debug("Found avatar for student with ID: {}", avatar.getStudent().getId());
        return avatar;
    }

    public Avatar updateAvatar(Avatar avatar) {
        logger.info("Was invoked method for update avatar with ID: {}", avatar.getId());
        if (!avatarRepository.existsById(avatar.getId())) {
            logger.error(NOT_FOUND_MESSAGE, avatar.getId());
            throw new IllegalArgumentException("Аватар с ID " + avatar.getId() + " не найден");
        }
        logger.warn("Updating avatar with ID: {}", avatar.getId());
        return avatarRepository.save(avatar);
    }

    public void deleteAvatar(Long id) {
        logger.info("Was invoked method for delete avatar with ID: {}", id);
        if (!avatarRepository.existsById(id)) {
            logger.error(NOT_FOUND_MESSAGE, id);
            throw new IllegalArgumentException("Аватар с ID " + id + " не найден");
        }
        logger.debug("Deleting avatar with ID: {}", id);
        avatarRepository.deleteById(id);
    }

    public Page<Avatar> getAllAvatars(int page, int size) {
        logger.info("Was invoked method for get all avatars, page: {}, size: {}", page, size);
        logger.debug("Retrieving avatars with page: {}, size: {}", page, size);
        return avatarRepository.findAll(PageRequest.of(page, size));
    }
}