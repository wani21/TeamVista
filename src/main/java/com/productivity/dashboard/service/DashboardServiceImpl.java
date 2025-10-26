package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.DashboardSummary;
import com.productivity.dashboard.dto.ProductivityScore;
import com.productivity.dashboard.model.Task;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.repository.TaskRepository;
import com.productivity.dashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of DashboardService for analytics calculations
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public DashboardSummary getSummary() {
        // Calculate basic metrics
        long totalTasks = taskRepository.countTotalTasks();
        long completedTasks = taskRepository.countCompletedTasks();
        long pendingTasks = taskRepository.countPendingTasks();

        // Calculate on-time completion percentage
        double onTimeCompletionPercent = calculateOnTimeCompletionPercent();

        // Calculate productivity scores per user
        List<ProductivityScore> productivityScores = calculateProductivityScores();

        return new DashboardSummary(
                totalTasks,
                completedTasks,
                pendingTasks,
                onTimeCompletionPercent,
                productivityScores
        );
    }

    /**
     * Calculate on-time completion percentage
     */
    private double calculateOnTimeCompletionPercent() {
        long totalCompletedTasks = taskRepository.countCompletedTasks();
        if (totalCompletedTasks == 0) {
            return 0.0;
        }

        List<Task> onTimeCompletedTasks = taskRepository.findOnTimeCompletedTasks();
        long onTimeCount = onTimeCompletedTasks.size();

        return (double) onTimeCount / totalCompletedTasks * 100.0;
    }

    /**
     * Calculate productivity scores for all users
     */
    private List<ProductivityScore> calculateProductivityScores() {
        List<User> allUsers = userRepository.findAll();
        List<ProductivityScore> scores = new ArrayList<>();

        for (User user : allUsers) {
            long totalUserTasks = taskRepository.countTasksByUserId(user.getId());
            long completedUserTasks = taskRepository.countCompletedTasksByUserId(user.getId());

            double score = 0.0;
            if (totalUserTasks > 0) {
                score = (double) completedUserTasks / totalUserTasks * 100.0;
            }

            ProductivityScore productivityScore = new ProductivityScore(
                    user.getId(),
                    user.getName(),
                    score,
                    totalUserTasks,
                    completedUserTasks
            );

            scores.add(productivityScore);
        }

        return scores;
    }
}