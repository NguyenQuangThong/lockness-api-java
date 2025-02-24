package com.example.LocknessAPI.services;

import com.example.LocknessAPI.commons.EntityStatus;
import com.example.LocknessAPI.commons.MessageDefine;
import com.example.LocknessAPI.models.Model;
import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.repositories.ModelRepository;
import com.example.LocknessAPI.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModelService {

    private final ModelRepository modelRepository;
    private final UserRepository userRepository;
    private final QueueService modelQueueService;

    public Model createModel(String prompt, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException(MessageDefine.USER_NOT_FOUND.getMessage()));
        Model model = com.example.LocknessAPI.models.Model.builder()
                .user(user)
                .prompt(prompt)
                .status(EntityStatus.PENDING.getCode())
                .build();
        modelRepository.save(model);
        queueModelProcessing(model);
        return model;
    }

    private void queueModelProcessing(Model model) {
        modelQueueService.dispatch(model.getId(), "model", 10, "exponential", 3000);
    }
}
