CREATE TABLE board (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

INSERT INTO board (name) VALUES ('v');

ALTER TABLE thread
ADD board_id INTEGER NULL REFERENCES board(id);

UPDATE thread SET board_id = (SELECT id FROM board WHERE name = 'v');

ALTER TABLE thread
ALTER board_id SET NOT NULL;
