-- ============================================================
-- Adaptive AI Coding Mentor — Database Schema
-- MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS ai_coding_mentor
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ai_coding_mentor;

-- ============================================================
-- Users
-- ============================================================
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role        ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    level       ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') NOT NULL DEFAULT 'BEGINNER',
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- ============================================================
-- Problems
-- ============================================================
CREATE TABLE problems (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    title                   VARCHAR(255) NOT NULL,
    description             TEXT NOT NULL,
    topic                   VARCHAR(50) NOT NULL,
    difficulty              ENUM('BEGINNER', 'EASY', 'MEDIUM', 'HARD', 'INTERVIEW') NOT NULL DEFAULT 'EASY',
    language                VARCHAR(20) NOT NULL DEFAULT 'java',
    starter_code            TEXT,
    generated_from_problem_id BIGINT NULL,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_problems_topic (topic),
    INDEX idx_problems_difficulty (difficulty),
    INDEX idx_problems_language (language),
    FOREIGN KEY (generated_from_problem_id) REFERENCES problems(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- Submissions
-- ============================================================
CREATE TABLE submissions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    problem_id  BIGINT NULL,
    language    VARCHAR(20) NOT NULL DEFAULT 'java',
    code        TEXT NOT NULL,
    status      ENUM('PENDING', 'CORRECT', 'INCORRECT', 'ERROR', 'TIMEOUT') NOT NULL DEFAULT 'PENDING',
    output      TEXT,
    ai_response TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_submissions_user (user_id),
    INDEX idx_submissions_problem (problem_id),
    INDEX idx_submissions_created (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- Mistakes
-- ============================================================
CREATE TABLE mistakes (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    submission_id   BIGINT NOT NULL,
    mistake_type    VARCHAR(100) NOT NULL,
    topic           VARCHAR(50) NOT NULL,
    line_number     INT,
    description     TEXT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_mistakes_user (user_id),
    INDEX idx_mistakes_type (mistake_type),
    INDEX idx_mistakes_topic (topic),
    INDEX idx_mistakes_created (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Hint Sessions (one per submission, tracks progressive hint level)
-- ============================================================
CREATE TABLE hint_sessions (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    submission_id       BIGINT NOT NULL,
    current_level       INT NOT NULL DEFAULT 1,
    solution_revealed   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_hint_sessions_user (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Hints (individual hint entries within a session)
-- ============================================================
CREATE TABLE hints (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    hint_session_id BIGINT NOT NULL,
    level           INT NOT NULL,
    content         TEXT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_hints_session (hint_session_id),
    FOREIGN KEY (hint_session_id) REFERENCES hint_sessions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Topic Mastery (Student Coding DNA)
-- ============================================================
CREATE TABLE topic_mastery (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    topic       VARCHAR(50) NOT NULL,
    score       DOUBLE NOT NULL DEFAULT 50.0,
    attempts    INT NOT NULL DEFAULT 0,
    successes   INT NOT NULL DEFAULT 0,
    common_mistakes JSON,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_topic (user_id, topic),
    INDEX idx_mastery_user (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- User Problem Progress
-- ============================================================
CREATE TABLE user_problem_progress (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    problem_id      BIGINT NOT NULL,
    solved          BOOLEAN NOT NULL DEFAULT FALSE,
    hints_used      INT NOT NULL DEFAULT 0,
    attempts        INT NOT NULL DEFAULT 0,
    completed_at    TIMESTAMP NULL,
    UNIQUE KEY uk_user_problem (user_id, problem_id),
    INDEX idx_progress_user (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
) ENGINE=InnoDB;
