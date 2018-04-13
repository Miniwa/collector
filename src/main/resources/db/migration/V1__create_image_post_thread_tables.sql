CREATE TABLE image (
    id SERIAL PRIMARY KEY,
    filename TEXT NOT NULL,
    original_filename TEXT NOT NULL,
    width INTEGER NOT NULL,
    height INTEGER NOT NULL,
    size INTEGER NOT NULL,
    md5 TEXT NOT NULL,
    deleted BOOLEAN NOT NULL,
    spoiler BOOLEAN NOT NULL
);

CREATE TABLE thread (
    id SERIAL PRIMARY KEY,
    archived BOOLEAN NOT NULL,
    archived_date TIMESTAMP NULL,
    closed BOOLEAN NOT NULL,
    sticky BOOLEAN NOT NULL,
    bump_limited BOOLEAN NOT NULL,
    image_limited BOOLEAN NOT NULL,
    subject TEXT NULL,
    semantic_url TEXT NULL,
    tag TEXT NULL
);

CREATE TABLE post (
    id SERIAL PRIMARY KEY,
    thread_id INTEGER NOT NULL REFERENCES Thread(Id),
    number INTEGER NOT NULL,
    submitted_date TIMESTAMP NOT NULL,
    name TEXT NULL,
    tripcode TEXT NULL,
    post_id TEXT NULL,
    pass_since_year INTEGER NULL,
    capcode TEXT NULL,
    comment TEXT NULL,
    image_id INTEGER NULL REFERENCES Image(Id)
);