CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    bio VARCHAR(500),
    profile_image_url VARCHAR(500),
    cover_image_url VARCHAR(500),
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL,
    is_deleted BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE posts (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    thumbnail_url VARCHAR(500),
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    view_count INTEGER NOT NULL,
    like_count INTEGER NOT NULL,
    comment_count INTEGER NOT NULL,
    bookmark_count INTEGER NOT NULL,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP,
    author_id UUID NOT NULL,
    category_id UUID,
    CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE tags (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_tags_created_by FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE TABLE comments (
    id UUID PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL,
    post_id UUID NOT NULL,
    parent_id UUID,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_id) REFERENCES comments (id)
);

CREATE TABLE post_likes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    post_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_post_likes_user_post UNIQUE (user_id, post_id),
    CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE TABLE comment_likes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    comment_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_comment_likes_user_comment UNIQUE (user_id, comment_id),
    CONSTRAINT fk_comment_likes_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_comment_likes_comment FOREIGN KEY (comment_id) REFERENCES comments (id)
);

CREATE TABLE bookmarks (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    post_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_bookmarks_user_post UNIQUE (user_id, post_id),
    CONSTRAINT fk_bookmarks_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_bookmarks_post FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    issued_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE post_tags (
    post_id UUID NOT NULL,
    tag_id UUID NOT NULL,
    CONSTRAINT pk_post_tags PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post_tags_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_post_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
);

CREATE INDEX idx_posts_author_id ON posts (author_id);
CREATE INDEX idx_posts_category_id ON posts (category_id);
CREATE INDEX idx_posts_status ON posts (status);
CREATE INDEX idx_comments_post_id ON comments (post_id);
CREATE INDEX idx_comments_user_id ON comments (user_id);
CREATE INDEX idx_comments_parent_id ON comments (parent_id);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
