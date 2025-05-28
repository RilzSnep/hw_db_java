package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Avatar createAvatar(@RequestBody Avatar avatar) {
        return avatarService.createAvatar(avatar);
    }

    @GetMapping("/{id}")
    public Avatar getAvatar(@PathVariable Long id) {
        return avatarService.getAvatar(id);
    }

    @PutMapping("/{id}")
    public Avatar updateAvatar(@PathVariable Long id, @RequestBody Avatar avatar) {
        avatar.setId(id);
        return avatarService.updateAvatar(avatar);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvatar(@PathVariable Long id) {
        avatarService.deleteAvatar(id);
    }

    @GetMapping
    public Page<Avatar> getAllAvatars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return avatarService.getAllAvatars(page, size);
    }
}