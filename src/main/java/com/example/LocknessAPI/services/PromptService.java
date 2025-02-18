package com.example.LocknessAPI.services;

import com.example.LocknessAPI.models.Prompt;
import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.repositories.PromptRepository;
import com.example.LocknessAPI.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final QueueService promptQueueService;

    public Prompt createPrompt(String text, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Prompt prompt = Prompt.builder()
                .user(user)
                .text(text)
                .status("pending")
                .build();
        promptRepository.save(prompt);
        queuePromptProcessing(prompt);
        return prompt;
    }

    private void queuePromptProcessing(Prompt prompt) {
        promptQueueService.dispatch(prompt.getId(), "prompt", 10, "exponential", 3000);
    }
}
