package com.zborowski.bartek.githubbrowser.repository.infrastructure.client;

import com.zborowski.bartek.githubbrowser.repository.domain.GithubRepository;
import com.zborowski.bartek.githubbrowser.repository.domain.GithubUserRepositories;
import com.zborowski.bartek.githubbrowser.repository.domain.GithubUserRepositoriesProvider;
import com.zborowski.bartek.githubbrowser.repository.domain.InvalidUserException;
import com.zborowski.bartek.githubbrowser.repository.infrastructure.dto.GithubRepositoriesDto;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class GithubUserRepositoriesClient implements GithubUserRepositoriesProvider {

    private static final String BASE_PATH = "https://api.github.com";
    private static final String USERS_PATH = "/users/";
    private static final String REPOS_PATH = "/repos";

    private final RestTemplate restTemplate;

    GithubUserRepositoriesClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GithubUserRepositories getUserRepositories(String userName) {
        String path = createPath(userName);
        GithubRepositoriesDto[] dtoRepositories;
        try {
            dtoRepositories = restTemplate.getForObject(path, GithubRepositoriesDto[].class);
        } catch (Exception exception) {
            throw new InvalidUserException(exception.getMessage());
        }
        return mapToGithubUserRepositories(userName, dtoRepositories);
    }

    private GithubUserRepositories mapToGithubUserRepositories(String userName, GithubRepositoriesDto[] dtoRepositories) {
        List<GithubRepository> repositories = Arrays.stream(dtoRepositories)
                .map(repository -> new GithubRepository(repository.getName(), repository.getStars()))
                .collect(Collectors.toList());
        return new GithubUserRepositories(repositories, userName);
    }

    private String createPath(String userName) {
        return BASE_PATH + USERS_PATH + userName + REPOS_PATH;
    }
}